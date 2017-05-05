package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.DomElement;

/**
 * @author Yang Tao, 17/3/3.
 */

public abstract class LayoutAttrHandler extends AttrHandler {
    public abstract void applyToChild(Context context, View v, DomElement domElement, View
            parent, ViewGroup.LayoutParams layoutParams, String params, Object value, boolean
            isParent) throws AttrApplyException;

    public void setDefaultToChild(Context context, View v, DomElement domElement, View parent,
                                  ViewGroup.LayoutParams layoutParams) throws AttrApplyException {

    }
}
