package com.mozz.remoteview;

import android.content.Context;
import android.webkit.WebView;

/**
 * @author Yang Tao, 17/3/8.
 */

public interface WebViewCreator extends ViewCreator<WebView> {
    WebView create(Context context);
}
