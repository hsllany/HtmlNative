package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.view.View;

import com.mozz.htmlnative.attrs.Attr;

/**
 * @author Yang Tao, 17/3/3.
 */

public interface RViewItem extends Attr {
    @NonNull
    Class<? extends View> onGetViewClassName();
}
