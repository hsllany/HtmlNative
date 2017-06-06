package com.mozz.htmlnative.css.stylehandler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.HtmlTag;
import com.mozz.htmlnative.css.InheritStylesRegistry;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.HNDiv;
import com.mozz.htmlnative.view.LayoutParamsCreator;

class HtmlLayoutStyleHandler extends StyleHandler {
    @Override
    public void apply(Context context, View v, DomElement domElement, View parent,
                      LayoutParamsCreator paramsCreator, String params, Object value)
            throws AttrApplyException {

        if (InheritStylesRegistry.isInherit(params)) {
            HNDiv div = (HNDiv) v;
            div.saveInheritStyles(params, value);
        }
    }

    @Override
    public void setDefault(Context context, View v, DomElement domElement,
                           LayoutParamsCreator paramsCreator, View parent) throws
            AttrApplyException {
        paramsCreator.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (domElement.getType().equals(HtmlTag.SPAN)) {
            paramsCreator.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            paramsCreator.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    @Override
    public Object getStyle(View v, String styleName) {
        return ((HNDiv) v).getInheritStyle(styleName);
    }
}
