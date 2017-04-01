package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;

/**
 * @author Yang Tao, 17/3/6.
 */

public class WebViewAttrHandler extends AttrHandler {
    @Override
    public void apply(Context context, java.lang.String tag, View v, @NonNull java.lang.String
            params, @NonNull final Object value, CharSequence innerElement) throws
            AttrApplyException {
        final WebView webView = (WebView) v;

        if (params.equals("src")) {
            webView.loadUrl(value.toString());
        }
    }
}
