package com.mozz.htmlnative.css.stylehandler;

import android.content.Context;
import android.view.View;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

/**
 * @author Yang Tao, 17/2/22.
 */

public abstract class StyleHandler {
    public abstract void apply(Context context, View v, DomElement domElement, View parent,
                               LayoutParamsLazyCreator paramsLazyCreator, String params, Object
                                       value, boolean isParent) throws AttrApplyException;

    public void setDefault(Context context, View v, DomElement domElement,
                           LayoutParamsLazyCreator paramsLazyCreator, View parent) throws
            AttrApplyException {

    }

    public Object getStyle(View v, String styleName) {
        return null;
    }
}
