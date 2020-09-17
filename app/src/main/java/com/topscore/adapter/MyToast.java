package com.topscore.adapter;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.topscore.vbowl.R;

/**
 * Created by SH376187 on 13-01-2019.
 */

public class MyToast {

    public static void myToast(Activity actvity, String msg){
        //Create custom dialog
        LayoutInflater inflater = actvity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_dialog,
                (ViewGroup) actvity.findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        ImageView imageView = (ImageView) layout.findViewById(R.id.image);
        text.setText(msg);
        if(msg.startsWith("Unable"))
            imageView.setImageResource(R.drawable.failed);
        else
            imageView.setImageResource(R.drawable.success);
        Toast toast = new Toast(actvity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}
