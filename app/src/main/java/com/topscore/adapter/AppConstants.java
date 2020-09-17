package com.topscore.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Shailendra on 4/6/2017.
 */

public class AppConstants {



   /* public static final String
*/



    public static final String PACKAGE_DURATION = "PackageDuration";
    private static AppConstants appConstants;
    private SharedPreferences sp;
    private Context context;
    public static boolean VOICE_VIEW_FLAG = false;
    public static boolean IS_PAUSE = false;

    private static String setUrl(String serverType) {
        String url;
        if (serverType.equalsIgnoreCase("prod")) {
            url = "https://miliu.wipro.com";
            return url;
        } else if (serverType.equalsIgnoreCase("aws")) {
            url = "https://miliucloud.com";
            return url;
        } else {
            url = "https://10.201.92.168";
            return url;

        }
    }

    private AppConstants(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static AppConstants getInstance(Context context) {

        return new AppConstants(context);
    }

    public void setKeyValue(String key, String value) {
        sp.edit().putString(key, value).commit();
    }


    public String getKeyValue(String key) {
        return sp.getString(key, "");

    }


}
