package com.topscore.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.topscore.vbowl.R;


/**
 * Created by SH376187 on 4/2/2019.
 */

public class FragmentUtility {
    public static void replaceFragment(int targetResourceID, FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(targetResourceID, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }


}
