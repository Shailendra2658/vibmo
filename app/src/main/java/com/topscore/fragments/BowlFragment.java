package com.topscore.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.topscore.vbowl.BowlTypeActivity;
import com.topscore.vbowl.R;
import com.topscore.vbowl.TestActivity;
import com.topscore.vbowl.TouchActivity;

import java.util.Locale;

import static com.topscore.vbowl.DeviceList.EXTRA_ADDRESS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BowlFragment.OnFragmentBowlnListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class BowlFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SEND = "SEND";


    private static final String TAG = "BowlTypeActivity";
    public static final String EXTRA_TYPE = "type";
    WebView offbreak, legbreak, yorker;
    Button buttonOffSpin, buttonLegSpin, buttonFast;
    TextToSpeech t1;
    Bundle data;
    TextView title, textViewTitle;
    ImageView imageViewBack;
    private OnFragmentBowlnListener mListener;

    public BowlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_bowl_type, container, false);
        // Set title bar

        data = getArguments();
        bindUI(view);
        // Inflate the layout for this fragment


        setOnclickListenerForAll();
        return view;
    }

    private void bindUI(final View view) {

        title = (TextView) view.findViewById(R.id.textViewPackageName);
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Light.ttf");
        textViewTitle.setTypeface(tf);
        offbreak = (WebView) view.findViewById(R.id.offbreak);
        legbreak = (WebView) view.findViewById(R.id.legbreak);
        yorker = (WebView) view.findViewById(R.id.yorker);
        imageViewBack = (ImageView) view.findViewById(R.id.imageViewBack);

        buttonFast = (Button) view.findViewById(R.id.buttonFast);
        buttonLegSpin = (Button) view.findViewById(R.id.buttonLegSpin);
        buttonOffSpin = (Button) view.findViewById(R.id.buttonOffspin);

        t1=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        title.setText(R.string.title_activity_bowl_type);
    }

    private void setOnclickListenerForAll() {
        offbreak.loadUrl("file:///android_asset/Off_break_small.gif");
        legbreak.loadUrl("file:///android_asset/Leg_break_small.gif");
        yorker.loadUrl("file:///android_asset/yorker.gif");
        offbreak.getSettings();
        offbreak.setBackgroundColor(getResources().getColor(R.color.green_color));
        legbreak.setBackgroundColor(getResources().getColor(R.color.green_color));
        yorker.setBackgroundColor(getResources().getColor(R.color.green_color));

        setTouchListener(offbreak,"in swing");
        setTouchListener(legbreak,"out swing");
        setTouchListener(yorker,"pace bowling");
        setImageViewTouchListener(imageViewBack,"back");


//        buttonFast.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // dataToSend("pace bowling");
//                //  t1.speak("You selected pace bowling", TextToSpeech.QUEUE_FLUSH, null);
//                onButtonPressed(SEND,"pace bowling");
//                goToTouchFragement("pace bowling", data.getString(EXTRA_ADDRESS));
////                Intent intent = new Intent(getActivity(), TouchActivity.class);
////                intent.putExtra(EXTRA_ADDRESS, data.getString(EXTRA_ADDRESS));
////                intent.putExtra(EXTRA_TYPE, "pace bowling");
//
//                //startActivity(intent);
//            }
//        });
//
//        buttonLegSpin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //  dataToSend("in swing");
//                //  t1.speak("You selected in swing", TextToSpeech.QUEUE_FLUSH, null);
//                onButtonPressed(SEND,"out swing");
//                goToTouchFragement("out swing", data.getString(EXTRA_ADDRESS));
////                Intent intent = new Intent(getContext(), TouchActivity.class);
////                intent.putExtra(EXTRA_ADDRESS, "");
////                intent.putExtra(EXTRA_TYPE, "in swing");
////
////                startActivity(intent);
//            }
//        });
//
//        buttonOffSpin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //  dataToSend("out swing");
//                // t1.speak("You selected out swing", TextToSpeech.QUEUE_FLUSH, null);
//
//                onButtonPressed(SEND,"in swing");
//                goToTouchFragement("in swing", data.getString(EXTRA_ADDRESS));
////                Intent intent = new Intent(getActivity(), TouchActivity.class);
////                intent.putExtra(EXTRA_ADDRESS, "");
////                intent.putExtra(EXTRA_TYPE, "out swing");
////                startActivity(intent);
//            }
//        });

//        yorker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // dataToSend("pace bowling");
//                //  t1.speak("You selected pace bowling", TextToSpeech.QUEUE_FLUSH, null);
//                onButtonPressed(SEND,"pace bowling");
//                goToTouchFragement("pace bowling", data.getString(EXTRA_ADDRESS));
////                Intent intent = new Intent(getActivity(), TouchActivity.class);
////                intent.putExtra(EXTRA_ADDRESS, data.getString(EXTRA_ADDRESS));
////                intent.putExtra(EXTRA_TYPE, "pace bowling");
//
//                //startActivity(intent);
//            }
//        });
//
//        legbreak.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //  dataToSend("in swing");
//                //  t1.speak("You selected in swing", TextToSpeech.QUEUE_FLUSH, null);
//                onButtonPressed(SEND,"in swing");
//                goToTouchFragement("in swing", data.getString(EXTRA_ADDRESS));
////                Intent intent = new Intent(getContext(), TouchActivity.class);
////                intent.putExtra(EXTRA_ADDRESS, "");
////                intent.putExtra(EXTRA_TYPE, "in swing");
////
////                startActivity(intent);
//            }
//        });
//
//        offbreak.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //  dataToSend("out swing");
//                // t1.speak("You selected out swing", TextToSpeech.QUEUE_FLUSH, null);
//
//                onButtonPressed(SEND,"out swing");
//                goToTouchFragement("out swing", data.getString(EXTRA_ADDRESS));
////                Intent intent = new Intent(getActivity(), TouchActivity.class);
////                intent.putExtra(EXTRA_ADDRESS, "");
////                intent.putExtra(EXTRA_TYPE, "out swing");
////                startActivity(intent);
//            }
//        });
    }

    private void setImageViewTouchListener(final ImageView imageViewBack, String s) {
        imageViewBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        imageViewBack.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        imageViewBack.setBackground(getActivity().getDrawable(R.color.green_color));
                        Intent intent = new Intent(getActivity(), TestActivity.class);
                        startActivity(intent);

                        //dataToSend(action);

                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        imageViewBack.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void setTouchListener(final WebView webView, final String action) {
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        webView.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        webView.setBackground(getActivity().getDrawable(R.color.green_color));

                        Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                        vibe.vibrate(100);
                        //msg(action);
                        // t1.speak(action, TextToSpeech.QUEUE_FLUSH, null);
                        onButtonPressed(SEND,action);
                        goToTouchFragement(action, data.getString(EXTRA_ADDRESS));
                        //dataToSend(action);

                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        webView.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                }
                return true;
            }
        });
    }

    public void goToTouchFragement(String type, String address){
 //       if(TestActivity.BOWL_FLAG) {
            TouchFragment fragment = new TouchFragment();
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_TYPE, type);
             bundle.putString(EXTRA_ADDRESS, address);
            fragment.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.moreFrame, fragment);
            ft.addToBackStack(fragment.getClass().getName());
            ft.commit();
//        }
//        else{
//            Toast.makeText(getActivity(),"Please wait...",Toast.LENGTH_LONG).show();
//        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String buttonName, String data) {
        if (mListener != null) {
            mListener.onFragmentBowl(buttonName, data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentBowlnListener) {
            mListener = (OnFragmentBowlnListener) context;
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
    public interface OnFragmentBowlnListener {

        void onFragmentBowl(String buttonName, String data);
    }
}
