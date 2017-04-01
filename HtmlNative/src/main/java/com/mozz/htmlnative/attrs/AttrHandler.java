package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;

/**
 * @author Yang Tao, 17/2/22.
 */

public abstract class AttrHandler {
    public abstract void apply(Context context, java.lang.String tag, View v, java.lang.String
            params, Object value, CharSequence innerElement, boolean isParent) throws AttrApplyException;

    public void setDefault(Context context, String tag, View v, CharSequence innerElement) throws
            AttrApplyException {

    }
}
