package com.topscore.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceService extends Service implements RecognitionListener{

    private static final String TAG = "VoiceService";
    private SpeechRecognizer speechRecognizer;
    TextToSpeech t1;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d("tag", "onCreate");
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        startListening();
    }

    public void startListening(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(this);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        speechRecognizer.startListening(intent);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d("tag", "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d("tag", "onStart");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("Speech", "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("Speech", "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("Speech", "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        String message = "";

        if(error == SpeechRecognizer.ERROR_AUDIO)                           message = "audio";
        else if(error == SpeechRecognizer.ERROR_CLIENT)                     message = "client";
        else if(error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)   message = "insufficient permissions";
        else if(error == SpeechRecognizer.ERROR_NETWORK)                    message = "network";
        else if(error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT)            message = "network timeout";
        else if(error == SpeechRecognizer.ERROR_NO_MATCH)                   message = "no match found";
        else if(error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY)            message = "recognizer busy";
        else if(error == SpeechRecognizer.ERROR_SERVER)                     message = "server";
        else if(error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)             message = "speech timeout";

        Log.d(TAG,"error " + message);
        //startListening();
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d("Speech", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d("Speech", "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("Speech", "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("Speech", "onResults");
        ArrayList strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < strlist.size();i++ ) {
            Log.d("Speech", "result=" + strlist.get(i));
            t1.speak(strlist.get(i).toString(), TextToSpeech.QUEUE_FLUSH, null);
        }
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter("mnt/sdcard/results.txt"));
//        out.write(processor.execute(strlist.get(0).toString()));
            out.write("hello world");
        } catch (IOException e) {
            Log.e("Speech",e.toString());
        }
        startListening();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d("Speech", "onRmsChanged");
    }
}
