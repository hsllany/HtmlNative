package com.mozz.htmlnative.attrshandler;

import android.content.Context;
import android.view.View;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

/**
 * @author Yang Tao, 17/3/3.
 */

public abstract class LayoutAttrHandler extends AttrHandler {
    public abstract void applyToChild(Context context, View v, DomElement domElement, View
            parent, LayoutParamsLazyCreator paramsLazyCreator, String params, Object value,
                                      boolean isParent) throws AttrApplyException;

    public void setDefaultToChild(Context context, View v, DomElement domElement, View parent,
                                  LayoutParamsLazyCreator paramsLazyCreator) throws
            AttrApplyException {

    }
}
