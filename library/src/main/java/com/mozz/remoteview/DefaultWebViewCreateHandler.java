package com.mozz.remoteview;

import android.content.Context;
import android.webkit.WebView;

/**
 * @author Yang Tao, 17/3/8.
 */

class DefaultWebViewCreateHandler implements WebViewCreateHandler {

    static DefaultWebViewCreateHandler sInstance;

    static {
        sInstance = new DefaultWebViewCreateHandler();
    }


    @Override
    public WebView create(Context context) {
        return new WebView(context);
    }
}
