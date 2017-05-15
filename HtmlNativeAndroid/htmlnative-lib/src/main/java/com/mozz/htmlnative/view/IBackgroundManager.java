package com.mozz.htmlnative.view;

import android.graphics.Bitmap;

import com.mozz.htmlnative.css.Background;

/**
 * @author Yang Tao, 17/5/9.
 */
interface IBackgroundManager {
    void setHtmlBackground(Bitmap bitmap, Background background);

    Background getHtmlBackground();
}
