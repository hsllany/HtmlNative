package com.mozz.htmlnative.stylehandler;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.mozz.htmlnative.css.InheritStylesRegistry;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.HNDiv;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

class HtmlLayoutStyleHandler extends StyleHandler {
    @Override
    public void apply(Context context, View v, DomElement domElement, View parent,
                      LayoutParamsLazyCreator paramsLazyCreator, String params, Object value,
                      boolean isParent) throws AttrApplyException {

        if (InheritStylesRegistry.isInherit(params)) {
            HNDiv div = (HNDiv) v;
            div.saveInheritStyles(params, value);
        }
    }

    @Override
    public void setDefault(Context context, View v, DomElement domElement,
                           LayoutParamsLazyCreator paramsLazyCreator, View parent) throws
            AttrApplyException {
        paramsLazyCreator.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        paramsLazyCreator.width = LinearLayout.LayoutParams.MATCH_PARENT;
    }

    @Override
    public Object getStyle(View v, String styleName) {
        return ((HNDiv) v).getInheritStyle(styleName);
    }
}
