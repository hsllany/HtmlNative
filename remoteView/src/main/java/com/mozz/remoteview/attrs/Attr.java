package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;

import com.mozz.remoteview.AttrApplyException;

/**
 * @author Yang Tao, 17/2/22.
 */

public interface Attr {
    void apply(Context context, View v, String params, Object value) throws AttrApplyException;
}
