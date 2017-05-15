package com.mozz.htmlnative.attrshandler;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

/**
 * @author Yang Tao, 17/3/6.
 */

class WebViewAttrHandler extends AttrHandler {

    private static final String ATTR_SRC = "src";

    @Override
    public void apply(Context context, View v, DomElement domElement, View parent,
                      LayoutParamsLazyCreator paramsLazyCreator, String params, Object value,
                      boolean isParent) throws AttrApplyException {
        final WebView webView = (WebView) v;

        if (params.equals(ATTR_SRC) && !isParent) {
            webView.loadUrl(value.toString());
        }
    }
}
