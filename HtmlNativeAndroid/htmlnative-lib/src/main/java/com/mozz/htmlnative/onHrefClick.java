package com.mozz.htmlnative;

import android.view.View;

/**
 * called when tag a with a href property is clicked.
 * You should set this via {@link HNativeEngine#setHrefLinkHandler(onHrefClick)}
 *
 * @author Yang Tao, 17/3/10.
 */

public interface onHrefClick {
    void onHref(String url, View view);
}
