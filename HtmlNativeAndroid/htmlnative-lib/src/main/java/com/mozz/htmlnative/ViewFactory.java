package com.mozz.htmlnative;

import android.content.Context;
import android.view.View;

/**
 * @author Yang Tao, 17/3/8.
 */

public interface ViewFactory<T extends View> {
    T create(Context context);
}
