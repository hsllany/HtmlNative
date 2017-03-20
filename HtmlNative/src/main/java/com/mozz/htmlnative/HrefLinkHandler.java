package com.mozz.htmlnative;

import android.view.View;

/**
 * called when tag a with a href property is clicked.
 * You should set this via {@link RV#setHrefLinkHandler(HrefLinkHandler)}
 *
 * @author Yang Tao, 17/3/10.
 */

public interface HrefLinkHandler {
    void onHref(String url, View view);
}
