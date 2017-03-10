package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.mozz.remoteview.AttrApplyException;
import com.mozz.remoteview.RVDomTree;

/**
 * @author Yang Tao, 17/3/6.
 */

public class WebViewAttr implements Attr {
    @Override
    public void apply(Context context, String tag, View v, String params, final Object value, RVDomTree tree)
            throws AttrApplyException {
        final WebView webView = (WebView) v;

        if (params.equals("src")) {
            webView.loadUrl(value.toString());
        }
    }
}
