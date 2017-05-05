package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;

/**
 * @author Yang Tao, 17/4/17.
 */

public class AbsoluteAttrHandler extends AttrHandler {
    @Override
    public void apply(Context context, View v, DomElement domElement, View parent, ViewGroup
            .LayoutParams layoutParams, String params, Object value, boolean isParent) throws
            AttrApplyException {

    }
}
