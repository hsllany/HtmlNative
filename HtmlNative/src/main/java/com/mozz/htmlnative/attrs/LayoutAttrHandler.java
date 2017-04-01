package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Yang Tao, 17/3/3.
 */

public abstract class LayoutAttrHandler extends AttrHandler {
    public abstract void applyToChild(Context context, String tag, View v, ViewGroup parent,
                                      String params, Object value) throws AttrApplyException;

    public void setDefaultToChild(Context context, String tag, View v, String innerElement)
            throws AttrApplyException {

    }
}
