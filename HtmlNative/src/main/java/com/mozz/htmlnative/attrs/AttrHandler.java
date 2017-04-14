package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Yang Tao, 17/2/22.
 */

public abstract class AttrHandler {
    public abstract void apply(Context context, String tag, View v, String params, Object value,
                               CharSequence innerElement, ViewGroup.LayoutParams layoutParams,
                               View parent, boolean isParent) throws AttrApplyException;

    public void setDefault(Context context, String tag, View v, CharSequence innerElement,
                           ViewGroup.LayoutParams layoutParams, View parent) throws
            AttrApplyException {

    }
}
