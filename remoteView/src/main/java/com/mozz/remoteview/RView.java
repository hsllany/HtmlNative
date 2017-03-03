package com.mozz.remoteview;

import android.view.View;

import com.mozz.remoteview.attrs.Attr;

/**
 * @author Yang Tao, 17/3/3.
 */

public interface RView extends Attr {
    Class<? extends View> onGetViewClassName();
}
