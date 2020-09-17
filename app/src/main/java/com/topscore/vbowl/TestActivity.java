package com.topscore.vbowl;

import android.app.Activity;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.topscore.adapter.AppConstants;
import com.topscore.adapter.MyToast;
import com.topscore.adapter.VoiceRecognizerDialogFragment;
import com.topscore.fragments.AdminFragment;
import com.topscore.fragments.BowlFragment;
import com.topscore.fragments.ChooserFragment;
import com.topscore.fragments.TouchFragment;
import com.topscore.fragments.VoiceFragment;
import com.topscore.reciever.MediaReceiver;
import com.topscore.service.BluetoothChatService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.topscore.fragments.ChooserFragment.REQ_CODE_SPEECH_INPUT;
import static com.topscore.fragments.ChooserFragment.VOICE;
import static com.topscore.vbowl.DeviceList.EXTRA_ADDRESS;
import static com.topscore.vbowl.DeviceListActivity.EXTRA_DEVICE_ADDRESS;

public class TestActivity extends AppCompatActivity implements ChooserFragment.OnFragmentInteractionListener,
        BowlFragment.OnFragmentBowlnListener, TouchFragment.OnFragmentTouchListener, VoiceFragment.OnFragmentVoiceListener,
        AdminFragment.OnFragmentAdminnListener,VoiceRecognizerDialogFragment.VoiceRecognizerInterface, RecognitionListener
{
    public static final String TAG = "TestActivity";
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private static final String ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION";

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static boolean BOWL_FLAG=false;

    //private Button mSendButton, mConnectButton;
    ImageView touch,mic;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    //Address
    String address = "";

    ViewStub stub;
    LinearLayout linearLayout;
    public int counter = 0;

    private List messageList = new ArrayList();

    private String mTemp = "";
    static TextToSpeech t1;

    String[] PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int PERMISSION_ALL = 1;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private PowerManager.WakeLock mWakeLock;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_more);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
// screen stays on in this section
       //
        // mWakeLock.release();

//        touch = (ImageView) findViewById(R.id.ImageViewTouch);
//        mic = (ImageView) findViewById(R.id.ImageViewMic);

      //  setOnclickListener();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Toast.makeText(getApplicationContext(), "Action clicked", Toast.LENGTH_LONG).show();
           connect();

            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                if (mChatService == null) setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }

        }

        int used = checkLaunchCount();
        if(used>80){
            finish();
        }

        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            if (mChatService == null) setupChat();
            if (mChatService != null) {
                if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                    mChatService.start();
                }
            }
        }

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


//        Set<String> a=new HashSet<>();
//        a.add("male");//here you can give male if you want to select mail voice.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Voice v = new Voice("en-us-x-sfg#male" + "-local", new Locale("en", "US"), 400, 200, true, a);
//            t1.setVoice(v);
//        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);//"android.intent.action.MEDIA_BUTTON"
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        MediaReceiver r = new MediaReceiver();
        filter.setPriority(filter.SYSTEM_HIGH_PRIORITY+1000); //this line sets receiver priority
        registerReceiver(r, filter);
        //sendMessage("Hellow");
        ChooserFragment ChooserFragment = new ChooserFragment();
        goToFragement(ChooserFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"Key DOWN...CODE "+keyCode+" Event "+event+ "Display "+event.getDisplayLabel());
        if(event.getKeyCode()== KeyEvent.KEYCODE_HEADSETHOOK){

//            /**
//             * Showing google speech input dialog
//             * */
 //       Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                getString(R.string.speech_prompt));
//        try {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
            showVoiceDialog();
        }

        //Toast.makeText(getApplicationContext(),"KEY PRESSED CODE "+keyCode+" Event "+event+ "Display "+event.getDisplayLabel(), Toast.LENGTH_LONG).show();
/**
 * Showing google speech input dialog
 * */
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                getString(R.string.speech_prompt));
//        try {
//            startActivityForResult(intent, ChooserFragment.REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChooserFragment chooserFragment = new ChooserFragment();
       // goToFragement(chooserFragment);
        Log.e(TAG, "onFragmentBackPress stop");
       // sendMessage("stop");
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setupChat() {
//        mConnectButton = (Button) findViewById(R.id.scan_button);
//        mConnectButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                connect(v);
//            }
//        });
//
//        mSendButton = (Button) findViewById(R.id.button_send);
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                String message = "Hardoceded#";
//                sendMessage(message);
//            }
//        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();

        //mChatService.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();

            if (mSpeechRecognizer != null) {
                mSpeechRecognizer.destroy();
            }
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    private void  sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
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
                    //String writeMessage = new String(writeBuf);
                    //msg("Before "+writeMessage);
                   //writeMessage = writeMessage.substring(begin, end);
                   // mAdapter.notifyDataSetChanged();
                   // messageList.add(new androidRecyclerView.Message(counter++, writeMessage, "Me"));
                   // msg(writeMessage);
                    break;
                case MESSAGE_READ:

                   // byte[] readBuf = (byte[]) msg.obj;
                    String readBuf = (String) msg.obj;

                    Log.d(TAG,"READING buffer 2" +
                            ""+readBuf);
                    // construct a string from the valid bytes in the buffer
                    String readMessage = readBuf;//new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "READING******"+readMessage);
                    if(readMessage.length()>0)
                        speakOut(readMessage);
                   // textReader(readMessage);
                    //t1.speak(readMessage, TextToSpeech.QUEUE_FLUSH, null);


//                    byte[] readBuf = (byte[]) msg.obj;
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mTemp = mTemp+readMessage;
//                    if(readMessage.endsWith("*")) {
//                        t1.speak(mTemp.substring(0,mTemp.length()-1), TextToSpeech.QUEUE_FLUSH, null);
//                        mTemp = "";
//                    }
                   // msg(readMessage);
                   // messageList.add(new androidRecyclerView.Message(counter++, readMessage, mConnectedDeviceName));
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to "
//                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    MyToast.myToast(TestActivity.this, "Paired with "+ mConnectedDeviceName);
                   // t1.speak(mConnectedDeviceName, TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case MESSAGE_TOAST:
                    if(msg.getData().getString(TOAST).equalsIgnoreCase("Unable to connect device")){
                       MyToast.myToast(TestActivity.this,  "Unable to pair with device");
                    }
                    else{
                        Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void speakOut(String readMessage) {

        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        for(char ch : alphabet){
            String str = ch+"";
            if(readMessage.equalsIgnoreCase(str)) {
                Log.d(TAG,"READING Matching found "+str);
                String speakThis = AppConstants.getInstance(getApplicationContext()).getKeyValue(str);
               // msg(speakThis);
                textReader(speakThis);
                //continue;
                break;
            }
        }

        if(readMessage.equalsIgnoreCase("b"))
            BOWL_FLAG=true;
        else if(readMessage.equalsIgnoreCase("a"))
            BOWL_FLAG = false;
    }

    private void textReader(String readMessage) {
        t1.setSpeechRate(0.8f);
       // t1.setPitch(1.0f);
        t1.speak(readMessage, TextToSpeech.QUEUE_FLUSH, null);
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    // Get the device MAC address
//                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//                    msg(address);
//                    this.address = address;
//                    Log.e(TAG, "Msg "+address);
//                    // Get the BLuetoothDevice object
//                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//                    // Attempt to connect to the device
//                    mChatService.connect(device);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                    // Bluetooth is now enabled, so set up a chat session
//                    setupChat();
//                } else {
//                    // User did not enable Bluetooth or an error occured
//                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//        }
//    }

    public void connect() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void discoverable(View v) {
        ensureDiscoverable();
    }

    public void goToFragement(ChooserFragment fragment){
        Bundle bundle = new Bundle();
       // bundle.putParcelable(AppConstants.DATA, data);
       // bundle.putInt(AppConstants.POSITION, position);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.moreFrame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();

    }




    @Override
    public void onBowlTypeInteraction(String buttonName, String address) {
        switch (buttonName) {
            case ChooserFragment.SETTINGS:
                this.address = address;
                Log.d(TAG, "onBowlTypeInteraction SETTINGS " + address);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device);
                break;
            case VOICE:
                Log.d(TAG, "onBowlTypeInteraction Voice " + address);
                if (!(address.equalsIgnoreCase("up") || address.equalsIgnoreCase("down") || address.equalsIgnoreCase("left")
                        || address.equalsIgnoreCase("right") || address.equalsIgnoreCase("fast") || address.equalsIgnoreCase("slow")
                        || address.equalsIgnoreCase("stop") || address.equalsIgnoreCase("reset") || address.equalsIgnoreCase("pace bowling")
                        || address.equalsIgnoreCase("inswing") || address.equalsIgnoreCase("outswing" )|| address.equalsIgnoreCase("shortpitch")
                        || address.equalsIgnoreCase("short pitch") || address.startsWith("short"))){
                    textReader("Invalid Keyword");
                }
                else if(address.equalsIgnoreCase("error") ){
                textReader("Unable to hear");
                }
                sendMessage(address);
                break;
        }


    }

    @Override
    public void onFragmentBowl(String buttonName, String data) {
        switch (buttonName){
            case BowlFragment.SEND:
                Log.d(TAG, "onFragmentBowl Bowl "+data);
                sendMessage(data);
                break;
        }
    }

    @Override
    public void onFragmentTouch(String buttonName, String data) {
        switch (buttonName){
            case TouchFragment.SEND:
                Log.d(TAG, "onFragmentTouch Touch "+data);
                sendMessage(data);
                break;

        }
    }

    @Override
    public void onFragmentVoice(String buttonName, String data) {
        switch (buttonName){
            case VoiceFragment.SEND:
                Log.d(TAG, "onFragmentVoice VOICE "+data);
                sendMessage(data);
                break;

        }
    }

    private int checkLaunchCount() {
        String data = readFromFile(getApplicationContext());
        Log.d(TAG,"readData "+data);
        int count = Integer.parseInt(data);
        count++;
        Log.d(TAG,"writeToFile "+count);
        writeToFile(""+count, getApplicationContext());
        return count;
    }



    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "1";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onFragmentAdmin(String buttonName, String data) {
        Log.e("onFragmentAdmin", "File not found: ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
//                    msg(address);
//                    this.address = address;
                    Log.e(TAG, "Msg "+address);
                    this.address = address;
                    this.address = address;
                    Log.d(TAG, "onBowlTypeInteraction SETTINGS "+address);
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
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(),result.get(0), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onBowlTypeInteraction Voice "+address);
                    sendMessage(result.get(0));
                }
                break;
        }
    }

    private void handleVoiceSearch(Intent intent) {
        if (intent != null && ACTION_VOICE_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG," Handle Voice "+query);
            //setSearchViewVisible(true);
           // searchView.setQuery(query, true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleVoiceSearch(intent);
    }

    @Override
    public void spokenText(String spokenText) {
        Toast.makeText(getApplicationContext(),spokenText,Toast.LENGTH_LONG).show();
    }

    public void showVoiceDialog(){
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        VoiceRecognizerDialogFragment languageDialogFragment = new VoiceRecognizerDialogFragment(getContext(), (VoiceRecognizerDialogFragment.VoiceRecognizerInterface) getContext());
//        languageDialogFragment.show(fragmentManager, "dialogVoiceRecognizer");
        //FragmentUtility.replaceFragment(R.id.moreFrame, getFragmentManager(), languageDialogFragment);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(TestActivity.this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        //Customize language by passing language code
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en");
        //To receive partial results on the callback
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getPackageName());
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        if(error == 7){
            Toast.makeText(TestActivity.this, "Unable to hear..", Toast.LENGTH_SHORT).show();
            textReader("Unable to hear");
        }
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(matches == null){
            return;
        }
        int i =0;
        String first ="";
        for(String s : matches){
            if(i==0){
                first = s;
            }
            i++;
        }
        // sending text to Bluetooth
        Toast.makeText(TestActivity.this,first, Toast.LENGTH_SHORT).show();
        if (!(address.equalsIgnoreCase("up") || address.equalsIgnoreCase("down") || address.equalsIgnoreCase("left")
                || address.equalsIgnoreCase("right") || address.equalsIgnoreCase("fast") || address.equalsIgnoreCase("slow")
                || address.equalsIgnoreCase("stop") || address.equalsIgnoreCase("reset") || address.equalsIgnoreCase("pace bowling")
                || address.equalsIgnoreCase("inswing") || address.equalsIgnoreCase("outswing") || address.startsWith("short"))){
            textReader("Invalid Keyword");
        }
        //onButtonPressed(VOICE, first);
        sendMessage(first);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }


}
