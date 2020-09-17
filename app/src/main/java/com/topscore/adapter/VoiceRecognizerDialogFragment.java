package com.topscore.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.topscore.vbowl.R;

import java.util.ArrayList;

/**
 * Created by SH376187 on 30-01-2019.
 */

public class VoiceRecognizerDialogFragment extends DialogFragment implements RecognitionListener {

    ImageView micImage;
    TextView stateTV;
    TextView displayTV;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Context context;
    VoiceRecognizerInterface signal;

    @SuppressLint("ValidFragment")
    public VoiceRecognizerDialogFragment(Context context, VoiceRecognizerInterface signal) {
        this.context = context;
        this.signal = signal;
    }

    public VoiceRecognizerDialogFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_voice_dialog ,container, false );

        //Mic tap to listen again
        micImage = view.findViewById(R.id.micImageView);
        //Displays Listening.. when recognizer is listening
        stateTV = view.findViewById(R.id.stateTV);
        //Displays message if error
        displayTV = view.findViewById(R.id.displayTV);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        //Customize language by passing language code
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en");
        //To receive partial results on the callback
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        micImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });
        startListening();
        return view;

    }

    public void startListening(){
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        changeUIStateToListening();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    public void onBeginningOfSpeech()
    {
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {

    }

    @Override
    public void onEndOfSpeech()
    {
    }

    @Override
    public void onError(int error)
    {
        if(error == 7){
            changeUIStateToRetry();
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onReadyForSpeech(Bundle params)
    {

    }

    @Override
    public void onResults(Bundle results)
    {
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
        // sending text to MainActivity using Interface
        signal.spokenText(first);
        this.dismiss();
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
    }

    public void changeUIStateToListening(){
        displayTV.setText("Tell us what you need");
        stateTV.setText("VIBO is Listening...");
    }

    public void changeUIStateToRetry(){
        displayTV.setText("Didn't catch that. Try\nSpeaking again");
        stateTV.setText("Tap on mic to try again");
    }



    public interface VoiceRecognizerInterface {
        void spokenText(String spokenText);
    }
}
