package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.remoteview.AttrApplyException;

/**
 * @author Yang Tao, 17/3/3.
 */

public interface LayoutAttr extends Attr {
    void applyToChild(Context context, String tag, View v, ViewGroup parent, String params, Object value)
            throws AttrApplyException;
}
