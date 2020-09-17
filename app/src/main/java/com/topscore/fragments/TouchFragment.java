package com.topscore.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.topscore.adapter.AppConstants;
import com.topscore.adapter.ImageModel;
import com.topscore.vbowl.R;
import com.topscore.vbowl.SlidingImage_Adapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.topscore.vbowl.DeviceList.EXTRA_ADDRESS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TouchFragment.OnFragmentTouchListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class TouchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SEND = "SEND";
    private static final String NAME = "BUTTONNAME";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Bundle data;
    private ArrayList<ImageModel> imageModelArrayList;
    ImageView up, down, right, left, more,less, imageViewStop, imageViewRestart, imageViewBack, imageViewPlay;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private TextView titleTextView, textViewTitle;

    private int[] myImageList = new int[]{R.drawable.slideone, R.drawable.slidetwo,
            R.drawable.slidethree, R.drawable.slidefour,R.drawable.slidefive};

    private OnFragmentTouchListener mListener;

    boolean delayFlag;

    public TouchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_touch, container, false);


        init(view);

        data = getArguments();
        setTouchListener(up, "short pitch");
        setTouchListener(down, "down");
        setTouchListener(left, "left");
        setTouchListener(right, "right");

        setTouchListener(more, "fast");
        setTouchListener(less, "slow");

        setTouchListener(imageViewRestart, "reset");
        setTouchListener(imageViewStop, "stop");

        setTouchListener(imageViewBack, "back");

        setTouchListener(imageViewPlay, "play");


        setTouchListener(imageViewPlay, "pause");


        return view;
    }




    private void init(View view) {
        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();
        more = (ImageView) view.findViewById(R.id.ImageViewMore);
        less = (ImageView) view.findViewById(R.id.ImageViewLess);

        left = (ImageView) view.findViewById(R.id.ImageViewLeft);
        right = (ImageView) view.findViewById(R.id.ImageViewRight);
        up = (ImageView) view.findViewById(R.id.ImageViewUp);
        down = (ImageView) view.findViewById(R.id.ImageViewDown);

        imageViewStop = (ImageView) view.findViewById(R.id.imageViewStop);
        imageViewRestart = (ImageView) view.findViewById(R.id.imageViewRestart);
        imageViewPlay = (ImageView) view.findViewById(R.id.imageViewPlay);

        imageViewBack = (ImageView) view.findViewById(R.id.imageViewBack);

        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(getActivity(),imageModelArrayList));

        CirclePageIndicator indicator = (CirclePageIndicator)
                view.findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Light.ttf");
        textViewTitle.setTypeface(tf);

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

    private void setTouchListener(final ImageView imgView, final String action) {
        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                       // imgView.setBackground(getActivity().getDrawable(R.color.secondary_color));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setImageBitmap(res.getDrawable(R.drawable.img_down));
                        //imgView.setBackground(getActivity().getDrawable(android.R.color.transparent));
                        if(action.equalsIgnoreCase("back")){
                           // goToBowlFragement(data.getString(EXTRA_ADDRESS));
                            goToChooseFragement();
                        } else if(action.equalsIgnoreCase("stop")){
                            onButtonPressed(SEND, action);
                            goToChooseFragement();
                        }
                        else {

                            if(!delayFlag) {
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        delayFlag = false;
                                    }
                                }, 1000);
                                delayFlag = true;
                                Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                vibe.vibrate(100);

                                if(action.equalsIgnoreCase("play") || action.equalsIgnoreCase("pause"))
                                    getCrtAction();
                                else
                                    onButtonPressed(SEND, action);
                            }
                            else{
                                Toast.makeText(getActivity(),"Please wait...",Toast.LENGTH_SHORT).show();
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        //imgView.setBackground(getActivity().getDrawable(R.color.secondary_color));

                        //v.setImageBitmap(res.getDrawable(R.drawable.img_up));
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void getCrtAction() {
        if (AppConstants.IS_PAUSE) {
            imageViewPlay.setImageResource(R.drawable.pause);
            AppConstants.IS_PAUSE = false;
            onButtonPressed(SEND, "play");

        } else {
            imageViewPlay.setImageResource(R.drawable.play);
            AppConstants.IS_PAUSE = true;
            onButtonPressed(SEND, "pause");
        }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String buttonName, String data) {
        if (mListener != null) {
            mListener.onFragmentTouch(buttonName, data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentTouchListener) {
            mListener = (OnFragmentTouchListener) context;
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
    public interface OnFragmentTouchListener {
        // TODO: Update argument type and name
        void onFragmentTouch(String buttonName, String data);
    }

    public void goToBowlFragement(String address){
        BowlFragment fragment = new BowlFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ADDRESS, address);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.moreFrame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public void goToChooseFragement(){
        ChooserFragment adminFragment = new ChooserFragment();
        Bundle bundle = new Bundle();
        // bundle.putParcelable(AppConstants.DATA, data);
        adminFragment.setArguments(bundle);
        FragmentUtility.replaceFragment(R.id.moreFrame, getFragmentManager(), adminFragment);
    }
}
