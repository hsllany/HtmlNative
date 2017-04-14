package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * @author Yang Tao, 17/3/6.
 */

public class WebViewAttrHandler extends AttrHandler {

    private static final String ATTR_SRC = "src";

    @Override
    public void apply(Context context, String tag, View v, String params, Object value,
                      CharSequence innerElement, ViewGroup.LayoutParams layoutParams, View
                                  parent, boolean isParent) throws AttrApplyException {
        final WebView webView = (WebView) v;

        if (params.equals("src") && !isParent) {
            webView.loadUrl(value.toString());
        }
    }
}
