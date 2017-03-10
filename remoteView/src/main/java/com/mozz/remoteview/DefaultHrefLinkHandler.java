package com.mozz.remoteview;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author Yang Tao, 17/3/11.
 */

class DefaultHrefLinkHandler implements HrefLinkHandler {

    @NonNull
    static final DefaultHrefLinkHandler sInstance;

    static {
        sInstance = new DefaultHrefLinkHandler();
    }

    @Override
    public void onHref(String url, @NonNull View view) {
        Intent intent = new Intent();
        intent.setAction("Android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        view.getContext().startActivity(intent);
    }
}
