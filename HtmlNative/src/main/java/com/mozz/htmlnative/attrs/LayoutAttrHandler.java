package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Yang Tao, 17/3/3.
 */

public abstract class LayoutAttrHandler extends AttrHandler {
    public abstract void applyToChild(Context context, String tag, View v, String params, Object
            value, CharSequence innerElement, ViewGroup.LayoutParams layoutParams, View parent,
                                      boolean isParent) throws AttrApplyException;

    public void setDefaultToChild(Context context, String tag, View v, CharSequence innerElement,
                                  ViewGroup.LayoutParams layoutParams, View parent) throws
            AttrApplyException {

    }
}
