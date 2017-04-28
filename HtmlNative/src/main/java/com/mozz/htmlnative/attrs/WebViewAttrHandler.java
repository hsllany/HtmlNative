package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mozz.htmlnative.DomElement;

/**
 * @author Yang Tao, 17/3/6.
 */

class WebViewAttrHandler extends AttrHandler {

    private static final String ATTR_SRC = "src";

    @Override
    public void apply(Context context, View v, DomElement domElement, View parent, ViewGroup
            .LayoutParams layoutParams, String params, Object value, boolean isParent) throws
            AttrApplyException {
        final WebView webView = (WebView) v;

        if (params.equals(ATTR_SRC) && !isParent) {
            webView.loadUrl(value.toString());
        }
    }
}
