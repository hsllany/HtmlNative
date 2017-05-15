package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.view.View;

import com.mozz.htmlnative.stylehandler.StyleHandler;

/**
 * @author Yang Tao, 17/3/3.
 */

public abstract class HNViewItem {
    @NonNull
    public abstract Class<? extends View> onGetViewClassName();

    public StyleHandler getHandler() {
        return null;
    }

    public Class<? extends View> getViewClass(){
        return null;
    }
}
