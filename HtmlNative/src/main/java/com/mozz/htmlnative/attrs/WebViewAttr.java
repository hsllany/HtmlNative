package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;

import com.mozz.htmlnative.AttrApplyException;
import com.mozz.htmlnative.RVDomElement;

/**
 * @author Yang Tao, 17/3/6.
 */

public class WebViewAttr implements Attr {
    @Override
    public void apply(Context context, String tag, View v, @NonNull String params, @NonNull final
    Object value, RVDomElement tree) throws AttrApplyException {
        final WebView webView = (WebView) v;

        if (params.equals("src")) {
            webView.loadUrl(value.toString());
        }
    }
}
