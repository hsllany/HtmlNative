package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;

import com.mozz.htmlnative.AttrApplyException;
import com.mozz.htmlnative.HNDomElement;

/**
 * @author Yang Tao, 17/2/22.
 */

public interface Attr {
    void apply(Context context, String tag, View v, String params, Object value, HNDomElement
            tree) throws AttrApplyException;
}
