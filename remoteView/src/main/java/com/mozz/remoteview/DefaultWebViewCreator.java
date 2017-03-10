package com.mozz.remoteview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.WebView;

/**
 * @author Yang Tao, 17/3/8.
 */

class DefaultWebViewCreator implements WebViewCreator {

    static DefaultWebViewCreator sInstance;

    static {
        sInstance = new DefaultWebViewCreator();
    }


    @NonNull
    @Override
    public WebView create(Context context) {
        return new WebView(context);
    }
}
