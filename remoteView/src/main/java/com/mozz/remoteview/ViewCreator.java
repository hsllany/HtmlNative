package com.mozz.remoteview;

import android.content.Context;
import android.view.View;

/**
 * @author Yang Tao, 17/3/8.
 */

public interface ViewCreator<T extends View> {
    T create(Context context);
}
