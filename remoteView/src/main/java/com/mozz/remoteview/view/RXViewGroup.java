package com.mozz.remoteview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Yang Tao, 17/3/8.
 */

public final class RXViewGroup extends FrameLayout {

    private static final String TAG = RXViewGroup.class.getSimpleName();

    @NonNull
    private List<WebView> mWebViewList = new LinkedList<>();

    public RXViewGroup(@NonNull Context context) {
        super(context);
    }

    @Override
    public void addView(View child) {
        super.addView(child);

        if (child instanceof WebView) {
            mWebViewList.add((WebView) child);
        }
    }

    public void addWebView(WebView v) {
        mWebViewList.add(v);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.d(TAG, "onDetachedFromWindow" + mWebViewList.toString());

        List<WebView> tempWebViewList = mWebViewList;
        mWebViewList.clear();

        for (WebView webView : tempWebViewList) {
            if (webView != null) {
                webView.destroy();
            }
        }
    }


}
