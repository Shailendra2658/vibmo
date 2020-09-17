package com.topscore.vbowl;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import static com.topscore.vbowl.DeviceList.EXTRA_ADDRESS;

public class BowlTypeActivity extends AppCompatActivity {

    private static final String TAG = "BowlTypeActivity";
    public static final String EXTRA_TYPE = "type";
    WebView offbreak, legbreak, yorker;
    Button buttonOffSpin, buttonLegSpin, buttonFast;
    TextToSpeech t1;
    BluetoothSocket btSocket;
    String address, type;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bowl_type);

        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      // GifWebView view = new GifWebView(this, "file:///android_asset    /offbreak.gif");
        offbreak.loadUrl("file:///android_asset/Off_break_small.gif");
        legbreak.loadUrl("file:///android_asset/Leg_break_small.gif");
        yorker.loadUrl("file:///android_asset/yorker.gif");

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device
        Log.d(TAG, "Address "+address);


        buttonFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // dataToSend("pace bowling");
              //  t1.speak("You selected pace bowling", TextToSpeech.QUEUE_FLUSH, null);

                Intent intent = new Intent(BowlTypeActivity.this, TouchActivity.class);
                intent.putExtra(EXTRA_ADDRESS, address);
                intent.putExtra(EXTRA_TYPE, "pace bowling");

                startActivity(intent);
            }
        });

        buttonLegSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  dataToSend("in swing");
              //  t1.speak("You selected in swing", TextToSpeech.QUEUE_FLUSH, null);

                Intent intent = new Intent(BowlTypeActivity.this, TouchActivity.class);
                intent.putExtra(EXTRA_ADDRESS, address);
                intent.putExtra(EXTRA_TYPE, "in swing");

                startActivity(intent);
            }
        });

        buttonOffSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  dataToSend("out swing");
               // t1.speak("You selected out swing", TextToSpeech.QUEUE_FLUSH, null);


                Intent intent = new Intent(BowlTypeActivity.this, TouchActivity.class);
                intent.putExtra(EXTRA_ADDRESS, address);
                intent.putExtra(EXTRA_TYPE, "out swing");
                startActivity(intent);
            }
        });

    }

    private void init() {
        offbreak = (WebView) findViewById(R.id.offbreak);
        legbreak = (WebView) findViewById(R.id.legbreak);
        yorker = (WebView) findViewById(R.id.yorker);

        buttonFast = (Button) findViewById(R.id.buttonFast);
        buttonLegSpin = (Button) findViewById(R.id.buttonLegSpin);
        buttonOffSpin = (Button) findViewById(R.id.buttonOffspin);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(1, bytes, -1, buffer)
                            .sendToTarget();

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    Disconnect();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] readBuf = (byte[]) msg.obj;;
            // construct a string from the valid bytes in the buffer
            String readMessage = new String(readBuf, 0, msg.arg1);
            t1.speak("VIBO says "+readMessage, TextToSpeech.QUEUE_FLUSH, null);

        }
    };

    private void dataToSend(String m_text) {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write(m_text.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
