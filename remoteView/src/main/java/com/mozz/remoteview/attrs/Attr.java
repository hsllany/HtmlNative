package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;

/**
 * Created by Yang Tao on 17/2/22.
 */

public interface Attr {
    void apply(Context context, View v, String params, Object value);
}
