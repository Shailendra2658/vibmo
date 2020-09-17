package com.topscore.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.topscore.adapter.AppConstants;
import com.topscore.vbowl.R;
import com.topscore.vbowl.TestActivity;

import java.util.Locale;

import static com.topscore.vbowl.DeviceList.EXTRA_ADDRESS;
import static com.topscore.vbowl.DeviceListActivity.EXTRA_DEVICE_ADDRESS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminFragment.OnFragmentAdminnListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SEND = "SEND";


    private static final String TAG = "AdminFragment";
    public static final String EXTRA_TYPE = "type";
    private ArrayAdapter mPairedDevicesArrayAdapter;
    private OnFragmentAdminnListener mListener;
    ImageView imgViewback;

    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_ser_test, container, false);
        imgViewback = (ImageView) view.findViewById(R.id.imageViewBack);
        mPairedDevicesArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
        // Find and set up the ListView for paired devices
        ListView newListView = (ListView) view.findViewById(R.id.new_list);
        newListView.setAdapter(mPairedDevicesArrayAdapter);
        newListView.setOnItemClickListener(mDeviceClickListener);
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        for(char ch : alphabet){
            mPairedDevicesArrayAdapter.add(ch);
            stDefaultValues(ch);
        }



        imgViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChooseFragement();
            }
        });
        return view;
    }

    private void stDefaultValues(char ch) {
        String str = ch+"";
        AppConstants.getInstance(getActivity()).setKeyValue(str,getValue(str));
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
        else if(str.equalsIgnoreCase("u"))
            val = getResources().getString(R.string.u);
        else if(str.equalsIgnoreCase("v"))
            val = getResources().getString(R.string.v);
        else if(str.equalsIgnoreCase("w"))
            val = getResources().getString(R.string.w);

        return val;
    }

    public void goToChooseFragement(){
        ChooserFragment adminFragment = new ChooserFragment();
        Bundle bundle = new Bundle();
        // bundle.putParcelable(AppConstants.DATA, data);
        adminFragment.setArguments(bundle);
        FragmentUtility.replaceFragment(R.id.moreFrame, getFragmentManager(), adminFragment);
    }

    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device
            String info = ((TextView) v).getText().toString();
            Log.d(TAG,"Clicked "+info);
            promptDialog(info);
        }
    };

    private void promptDialog(final String info_key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter value");

// Set up the input
        final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        input.setText(AppConstants.getInstance(getActivity()).getKeyValue(info_key));
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text_value = input.getText().toString();
                AppConstants.getInstance(getContext()).setKeyValue(info_key, m_Text_value);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String buttonName, String data) {
        if (mListener != null) {
            mListener.onFragmentAdmin(buttonName, data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentAdminnListener) {
            mListener = (OnFragmentAdminnListener) context;
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
    public interface OnFragmentAdminnListener {

        void onFragmentAdmin(String buttonName, String data);
    }
}
