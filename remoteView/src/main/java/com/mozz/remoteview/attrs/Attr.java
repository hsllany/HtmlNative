package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;

import com.mozz.remoteview.AttrApplyException;
import com.mozz.remoteview.RVDomTree;

/**
 * @author Yang Tao, 17/2/22.
 */

public interface Attr {
    void apply(Context context, String tag, View v, String params, Object value,
               RVDomTree tree) throws AttrApplyException;
}
