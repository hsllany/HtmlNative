package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;

import com.mozz.htmlnative.AttrApplyException;

/**
 * @author Yang Tao, 17/2/22.
 */

public abstract class Attr {
    public abstract void apply(Context context, java.lang.String tag, View v, java.lang.String
            params, Object value, String innerElement) throws AttrApplyException;

    public void setDefault(Context context, String tag, View v, String innerElement) throws
            AttrApplyException {
        
    }
}
