package com.topscore.vbowl;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by SH376187 on 03-01-2019.
 */

class GifWebView extends WebView{

    public GifWebView(Context context, String path) {
        super(context);

        loadUrl(path);
    }
}
