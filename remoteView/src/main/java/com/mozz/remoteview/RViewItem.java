package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.view.View;

import com.mozz.remoteview.attrs.Attr;

/**
 * @author Yang Tao, 17/3/3.
 */

public interface RViewItem extends Attr {
    @NonNull
    Class<? extends View> onGetViewClassName();
}
