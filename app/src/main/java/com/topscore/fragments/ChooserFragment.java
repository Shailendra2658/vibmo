package com.topscore.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.topscore.adapter.AppConstants;
import com.topscore.adapter.VoiceRecognizerDialogFragment;
import com.topscore.vbowl.DeviceListActivity;
import com.topscore.vbowl.R;
import com.topscore.vbowl.TestActivity;
import com.topscore.vbowl.VoiceActivity;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.topscore.adapter.AppConstants.VOICE_VIEW_FLAG;
import static com.topscore.vbowl.DeviceList.EXTRA_ADDRESS;
import static com.topscore.vbowl.TestActivity.REQUEST_CONNECT_DEVICE;
import static com.topscore.vbowl.TestActivity.REQUEST_ENABLE_BT;
import static com.topscore.vbowl.TestActivity.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChooserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ChooserFragment extends Fragment implements RecognitionListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String SETTINGS = "settings";
    public static final String VOICE = "voice";
    public static final int REQ_CODE_SPEECH_INPUT = 100;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageView settingButton, imageViewBDevices, imageViewLogo, ImageViewVoice, imageViewBack;
    private TextView titleTextView, textViewTitle;
    FloatingActionButton floatingActionButton;
    RelativeLayout relative_layout_main, relative_layout_second;
    //private Button mSendButton, mConnectButton;
    ImageView touch,mic;
    String address = "";
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private PowerManager.WakeLock mWakeLock;


    public ChooserFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mWakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
// screen stays on in this section
      //  mWakeLock.release();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.activity_test, container, false);
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        for(char ch : alphabet){
            stDefaultValues(ch);
        }
        bindUI(view);
        // Inflate the layout for this fragment


        setOnclickListenerForAll();
        return view;
    }

    private void bindUI(final View view) {

        settingButton = (ImageView) view.findViewById(R.id.settingButton) ;
        titleTextView = (TextView) view.findViewById(R.id.textViewPackageName);
        textViewTitle = (TextView) view.findViewById(R.id.textViewSelect);
        imageViewBDevices = (ImageView) view.findViewById(R.id.imageViewBDevices);
        imageViewLogo  = (ImageView) view.findViewById(R.id.imageViewLogo);
        ImageViewVoice  = (ImageView) view.findViewById(R.id.ImageViewVoice);
        imageViewBack = (ImageView) view.findViewById(R.id.imageViewBack);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Light.ttf");
        textViewTitle.setTypeface(tf);
        titleTextView.setText(getString(R.string.title_activity_touch));
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);
        touch = (ImageView) view.findViewById(R.id.ImageViewTouch);
        mic = (ImageView) view.findViewById(R.id.ImageViewMic);

        relative_layout_main = (RelativeLayout) view.findViewById(R.id.relative_layout_main);
        relative_layout_second = (RelativeLayout) view.findViewById(R.id.relative_layout_second);
    }

    private void setOnclickListenerForAll() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "CLICK ");

                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  stub.setLayoutResource(R.layout.content_bowl_type);
                // View inflated = stub.inflate();
            }
        });

        touch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                       // touch.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));

                        //    touch.setBackground(getActivity().getDrawable(android.R.color.transparent));
                            BowlFragment bowlFragment = new BowlFragment();
                            goToBowlFragement(bowlFragment);

                       // onButtonPressed(TOUCH, "");
                        //onButtonPressed(SETTINGS, "32:E0:1E:E8:62:61");

                        //  if(!address.equalsIgnoreCase("")) {

//                            Intent intent = new Intent(TestActivity.this, BowlTypeActivity.class);
//                            intent.putExtra(EXTRA_ADDRESS, address);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                        }
//                        else{
//                            msg("Please connect to Bluetooth Device from setting");
//                            Intent intent = new Intent(TestActivity.this, BowlTypeActivity.class);
//                            //startActivity(intent);
//                        }
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                      //  touch.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                }
                return true;
            }
        });

        //**Seting for Mic

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                       // mic.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                      //  mic.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        VOICE_VIEW_FLAG = true;
                        setVoiceView(VOICE_VIEW_FLAG);
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                     //   mic.setBackground(getActivity().getDrawable(android.R.color.transparent));
//                        VoiceFragment voiceFragment = new VoiceFragment();
//                        goToVoiceFragement(voiceFragment);
                        //promptSpeechInput();
                        //   if(!address.equalsIgnoreCase("")) {
//                        Intent intent = new Intent(getContext(), VoiceActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra(EXTRA_ADDRESS, "");
//                        //startActivity(intent);
//                        onButtonPressed(TOUCH, "");

//                        }
//                        else{
//                            msg("Please connect to Bluetooth Device from setting");
//                        }
                        break;
                    }
                }
                return true;
            }
        });

        imageViewBDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageViewBDevices.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                      //  imageViewBDevices.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                  //      imageViewBDevices.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                      //  imageViewBDevices.setBackground(getActivity().getDrawable(android.R.color.transparent));
                        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                        getActivity().startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//
                        break;
                    }
                }
                return true;
            }
        });

        imageViewLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AdminFragment adminFragment = new AdminFragment();
                    Bundle bundle = new Bundle();
                    // bundle.putParcelable(AppConstants.DATA, data);
                    bundle.putString(VoiceFragment.ADDRESS, address);
                adminFragment.setArguments(bundle);
                    FragmentUtility.replaceFragment(R.id.moreFrame, getFragmentManager(), adminFragment);
                return false;

            }
        });

        ImageViewVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageViewVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        //  imageViewBDevices.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        //      imageViewBDevices.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        //  imageViewBDevices.setBackground(getActivity().getDrawable(android.R.color.transparent));
                       // promptSpeechInput();
                        showVoiceDialog();
                        break;
                    }
                }
                return true;
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageViewBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        //  imageViewBDevices.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        //      imageViewBDevices.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        VOICE_VIEW_FLAG = false;
                      setVoiceView(VOICE_VIEW_FLAG);
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        //  imageViewBDevices.setBackground(getActivity().getDrawable(android.R.color.transparent));

                        break;
                    }
                }
                return true;
            }
        });

    }

    private void setVoiceView(boolean voiceViewFlag) {
        if(voiceViewFlag) {
            relative_layout_main.setVisibility(View.GONE);
            relative_layout_second.setVisibility(View.VISIBLE);
        }
        else{
            relative_layout_main.setVisibility(View.VISIBLE);
            relative_layout_second.setVisibility(View.GONE);
        }
    }

    /**
     * Showing google speech input dialog
     * */
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "VIBO is listening..");
        try {
            getActivity().startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void goToVoiceFragement(VoiceFragment fragment){
        Bundle bundle = new Bundle();
        // bundle.putParcelable(AppConstants.DATA, data);
        bundle.putString(VoiceFragment.ADDRESS, address);
        fragment.setArguments(bundle);
        FragmentUtility.replaceFragment(R.id.moreFrame, getFragmentManager(), fragment);

    }



    public void goToBowlFragement(BowlFragment fragment){
        Bundle bundle = new Bundle();
        // bundle.putParcelable(AppConstants.DATA, data);
        bundle.putString(EXTRA_ADDRESS, address);
        fragment.setArguments(bundle);
        FragmentUtility.replaceFragment(R.id.moreFrame, getFragmentManager(), fragment);
//        ft.replace(R.id.moreFrame, fragment);
//        ft.addToBackStack(fragment.getClass().getName());
      //  ft.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String buttonName, String mac) {
        if (mListener != null) {
            mListener.onBowlTypeInteraction(buttonName, mac);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume ChooserFragment");
        setVoiceView(VOICE_VIEW_FLAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//                    msg(address);
//                    this.address = address;
                    Log.e(TAG, "Msg "+address);
                    this.address = address;
                    onButtonPressed(SETTINGS, address);

                    // Get the BLuetoothDevice object
                   // BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                   // mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getActivity(),result.get(0), Toast.LENGTH_SHORT).show();
                    onButtonPressed(VOICE, result.get(0));
                }
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBowlTypeInteraction(String buttonName, String address);
    }

    private void stDefaultValues(char ch) {
        String str = ch+"";
        AppConstants.getInstance(getContext()).setKeyValue(str,getValue(str));
    }

    private String getValue(String str) {
        String val = "";
        val = AppConstants.getInstance(getActivity()).getKeyValue(str);
        if(val.length()>0)
            return val;

        if(str.equalsIgnoreCase("a"))
            val = getResources().getString(R.string.a);
        else if(str.equalsIgnoreCase("b"))
            val = getResources().getString(R.string.b);
        else if(str.equalsIgnoreCase("c"))
            val = getResources().getString(R.string.c);
        else if(str.equalsIgnoreCase("d"))
            val = getResources().getString(R.string.d);
        else if(str.equalsIgnoreCase("e"))
            val = getResources().getString(R.string.e);
        else if(str.equalsIgnoreCase("f"))
            val = getResources().getString(R.string.f);
        else if(str.equalsIgnoreCase("g"))
            val = getResources().getString(R.string.g);
        else if(str.equalsIgnoreCase("h"))
            val = getResources().getString(R.string.h);
        else if(str.equalsIgnoreCase("i"))
            val = getResources().getString(R.string.i);
        else if(str.equalsIgnoreCase("j"))
            val = getResources().getString(R.string.j);
        else if(str.equalsIgnoreCase("k"))
            val = getResources().getString(R.string.k);
        else if(str.equalsIgnoreCase("l"))
            val = getResources().getString(R.string.l);
        else if(str.equalsIgnoreCase("m"))
            val = getResources().getString(R.string.m);
        else if(str.equalsIgnoreCase("n"))
            val = getResources().getString(R.string.n);
        else if(str.equalsIgnoreCase("o"))
            val = getResources().getString(R.string.o);
        else if(str.equalsIgnoreCase("p"))
            val = getResources().getString(R.string.p);
        else if(str.equalsIgnoreCase("q"))
            val = getResources().getString(R.string.q);
        else if(str.equalsIgnoreCase("r"))
            val = getResources().getString(R.string.r);
        else if(str.equalsIgnoreCase("s"))
            val = getResources().getString(R.string.s);
        else if(str.equalsIgnoreCase("t"))
            val = getResources().getString(R.string.t);

        return val;
    }

    public void showVoiceDialog(){
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        VoiceRecognizerDialogFragment languageDialogFragment = new VoiceRecognizerDialogFragment(getContext(), (VoiceRecognizerDialogFragment.VoiceRecognizerInterface) getContext());
//        languageDialogFragment.show(fragmentManager, "dialogVoiceRecognizer");
        //FragmentUtility.replaceFragment(R.id.moreFrame, getFragmentManager(), languageDialogFragment);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        //Customize language by passing language code
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en");
        //To receive partial results on the callback
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getContext().getPackageName());
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
            Toast.makeText(getContext(), "Unable to hear..", Toast.LENGTH_SHORT).show();
            onButtonPressed(VOICE, "error");
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
        Toast.makeText(getActivity(),first, Toast.LENGTH_SHORT).show();
        onButtonPressed(VOICE, first);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }
}
