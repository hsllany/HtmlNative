package com.mozz.htmlnative.css.stylehandler;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.mozz.htmlnative.css.InheritStylesRegistry;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsCreator;

/**
 * @author Yang Tao, 17/3/6.
 */

class WebViewStyleHandler extends StyleHandler {

    private static final String ATTR_SRC = "src";

    static {
        // to protect the build-in styles
        InheritStylesRegistry.preserve(ATTR_SRC);
    }

    @Override
    public void apply(Context context, View v, DomElement domElement, View parent,
                      LayoutParamsCreator paramsCreator, String params, Object value)
            throws AttrApplyException {
        final WebView webView = (WebView) v;

        if (params.equals(ATTR_SRC)) {
            webView.loadUrl(value.toString());
        }
    }
}
