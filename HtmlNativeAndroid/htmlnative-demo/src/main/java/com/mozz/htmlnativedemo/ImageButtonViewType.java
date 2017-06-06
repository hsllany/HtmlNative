package com.mozz.htmlnativedemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.mozz.htmlnative.HNViewType;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

/**
 * @author Yang Tao, 17/6/6.
 */

public class ImageButtonViewType extends HNViewType<ImageView> {

    @NonNull
    @Override
    public Class<ImageView> getViewClass() {
        return null;
    }

    @NonNull
    @Override
    public String getHTMLType() {
        return null;
    }
    
    @Override
    public void onSetStyle(Context context, View v, View parent, LayoutParamsLazyCreator
            layoutCreator, String styleName, Object style) {

    }

    @Override
    public void onSetDefaultStyle(Context context, View v, LayoutParamsLazyCreator
            layoutParamsLazyCreator, View parent) {

    }

    @Override
    public ImageView create(Context context) {
        return null;
    }
}
