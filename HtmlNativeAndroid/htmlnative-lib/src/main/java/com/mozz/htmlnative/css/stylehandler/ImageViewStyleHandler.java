package com.mozz.htmlnative.css.stylehandler;

import android.content.Context;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.HNativeEngine;
import com.mozz.htmlnative.css.Background;
import com.mozz.htmlnative.css.InheritStylesRegistry;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.BackgroundViewDelegate;
import com.mozz.htmlnative.view.LayoutParamsCreator;

class ImageViewStyleHandler extends StyleHandler {

    static {
        // to protect the build-in styles
        InheritStylesRegistry.preserve("src");
    }

    @Override
    public void apply(Context context, View v, DomElement domElement, View parent,
                      LayoutParamsCreator paramsCreator, String params, Object value)
            throws AttrApplyException {
        if (params.equals("src") && HNativeEngine.getImageViewAdapter() != null) {
            Matrix matrix = null;
            String url = value.toString();
            Background background = null;
            if (value instanceof Background) {
                matrix = Background.createBitmapMatrix((Background) value);
                url = ((Background) value).getUrl();
                background = (Background) value;
            }

            HNativeEngine.getImageViewAdapter().setImage(url, new BackgroundViewDelegate(v,
                    matrix, background, true));
        }
    }

    @Override
    public void setDefault(Context context, View v, DomElement domElement,
                           LayoutParamsCreator paramsCreator, View parent) throws
            AttrApplyException {
        super.setDefault(context, v, domElement, paramsCreator, parent);

        paramsCreator.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }
}
