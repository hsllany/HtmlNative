package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.view.View;

import com.mozz.htmlnative.attrs.AttrHandler;

/**
 * @author Yang Tao, 17/3/3.
 */

public abstract class HNViewItem extends AttrHandler {
    @NonNull
    public abstract Class<? extends View> onGetViewClassName();
}
