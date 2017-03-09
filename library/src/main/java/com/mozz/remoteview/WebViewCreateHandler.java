package com.mozz.remoteview;

import android.content.Context;
import android.webkit.WebView;

/**
 * @author Yang Tao, 17/3/8.
 */

public interface WebViewCreateHandler extends ViewCreateHandler<WebView> {
    WebView create(Context context);
}
