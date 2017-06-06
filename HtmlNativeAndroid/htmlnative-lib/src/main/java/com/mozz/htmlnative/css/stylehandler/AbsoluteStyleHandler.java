package com.mozz.htmlnative.css.stylehandler;

import android.content.Context;
import android.view.View;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsCreator;

/**
 * @author Yang Tao, 17/4/17.
 */

public class AbsoluteStyleHandler extends StyleHandler {
    @Override
    public void apply(Context context, View v, DomElement domElement, View parent,
                      LayoutParamsCreator paramsCreator, String params, Object value)
            throws AttrApplyException {

    }
}
