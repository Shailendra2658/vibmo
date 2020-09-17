package com.topscore.vbowl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.topscore.adapter.ImageModel;
import com.topscore.service.BluetoothChatService;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.topscore.vbowl.DeviceList.EXTRA_ADDRESS;
import static com.topscore.vbowl.TestActivity.DEVICE_NAME;
import static com.topscore.vbowl.TestActivity.MESSAGE_DEVICE_NAME;
import static com.topscore.vbowl.TestActivity.MESSAGE_READ;
import static com.topscore.vbowl.TestActivity.MESSAGE_TOAST;
import static com.topscore.vbowl.TestActivity.MESSAGE_WRITE;
import static com.topscore.vbowl.TestActivity.REQUEST_CONNECT_DEVICE;
import static com.topscore.vbowl.TestActivity.REQUEST_ENABLE_BT;
import static com.topscore.vbowl.TestActivity.TOAST;


public class TouchActivity extends AppCompatActivity  {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    ImageView up, down, right, left, more,less;


    private static final String TAG = "TouchActivity";

    String address, type;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket;
    private boolean isBtConnected = false;
    static TextToSpeech t1;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Name of the connected device
    private String mConnectedDeviceName = null;

    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    private int[] myImageList = new int[]{R.drawable.slideone, R.drawable.slidetwo,
            R.drawable.slidethree, R.drawable.slidefour,R.drawable.slidefive};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        init();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
// If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setupChat();
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device
        type = newint.getStringExtra(BowlTypeActivity.EXTRA_TYPE);

        if(!address.equalsIgnoreCase("")){
            //new TouchActivity.ConnectBT().execute(); //Call the class to connect
        }
        Log.d(TAG, "Adreeess "+address);


        //dataToSend(type);
        setTouchListener(up, "up");
        setTouchListener(down, "down");
        setTouchListener(left, "left");
        setTouchListener(right, "right");

        setTouchListener(more, "fast");
        setTouchListener(less, "slow");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(TouchActivity.this, StartActivity.class);

        //Change the activity.
        i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
        startActivity(i);

    }

    @Override
    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }

    }

    private void setTouchListener(final ImageView imgView, final String action) {
        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        imgView.setBackground(getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                            imgView.setBackground(getDrawable(android.R.color.transparent));

                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        vibe.vibrate(100);
                        msg(action);
                        t1.speak(action, TextToSpeech.QUEUE_FLUSH, null);
                        sendMessage(action);

                            //dataToSend(action);

                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        imgView.setBackground(getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                }
                return true;
            }
        });
    }

    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 4; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }

    private void init() {
        more = (ImageView) findViewById(R.id.ImageViewMore);
        less = (ImageView) findViewById(R.id.ImageViewLess);

        left = (ImageView) findViewById(R.id.ImageViewLeft);
        right = (ImageView) findViewById(R.id.ImageViewRight);
        up = (ImageView) findViewById(R.id.ImageViewUp);
        down = (ImageView) findViewById(R.id.ImageViewDown);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(TouchActivity.this,imageModelArrayList));

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(TouchActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                    // Start the thread to manage the connection and perform transmissions
                      TouchActivity.ConnectedThread mConnectedThread = new TouchActivity.ConnectedThread(btSocket);
                      mConnectedThread.start();
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                isBtConnected = false;

                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }



    private void dataToSend(String m_text) {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write(m_text.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
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
//    private static final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            byte[] readBuf = (byte[]) msg.obj;;
//            // construct a string from the valid bytes in the buffer
//            String readMessage = new String(readBuf, 0, msg.arg1);
//            t1.speak(readMessage, TextToSpeech.QUEUE_FLUSH, null);
//
//        }
//    };

    //Code to Blue tooth
    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected+" Address "+address, Toast.LENGTH_SHORT).show();
              msg(address);
           // this.address = address;
            Log.e(TAG, "Not Connected connecting againg "+address);
            // Get the BLuetoothDevice object
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            mChatService.connect(device);
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
                mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);

        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // byte[] writeBuf = (byte[]) msg.obj;
            //  int begin = (int)msg.arg1;
            // int end = (int)msg.arg2;

            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //msg("Before "+writeMessage);
                    //writeMessage = writeMessage.substring(begin, end);
                    // mAdapter.notifyDataSetChanged();
                    // messageList.add(new androidRecyclerView.Message(counter++, writeMessage, "Me"));
                    msg("TouchActivity After "+writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    int beginr = (int)msg.arg1;
                    int endr = (int)msg.arg2;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //mAdapter.notifyDataSetChanged();
                    msg(readMessage);
                    // messageList.add(new androidRecyclerView.Message(counter++, readMessage, mConnectedDeviceName));
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void setupChat() {

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    msg(address);
                    this.address = address;
                    Log.e(TAG, "Msg "+address);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
