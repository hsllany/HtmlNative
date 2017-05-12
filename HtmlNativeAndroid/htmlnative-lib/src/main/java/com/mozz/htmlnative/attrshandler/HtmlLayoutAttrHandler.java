package com.mozz.htmlnative.attrshandler;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

class HtmlLayoutAttrHandler extends AttrHandler {
    @Override
    public void apply(Context context, View v, DomElement domElement, View parent, LayoutParamsLazyCreator paramsLazyCreator, String params, Object value, boolean isParent) throws
            AttrApplyException {

        //        LinearLayout l = (LinearLayout) v;
        //
        //        switch (params) {
        //            case "text-align":
        //                if (value.toString().equals("center")) {
        //                    l.setGravity(Gravity.CENTER);
        //                } else if (value.toString().equals("left")) {
        //                    l.setGravity(Gravity.START);
        //                } else if (value.toString().equals("right")) {
        //                    l.setGravity(Gravity.END);
        //                }
        //                break;
        //
        //        }
    }

    @Override
    public void setDefault(Context context, View v, DomElement domElement, LayoutParamsLazyCreator paramsLazyCreator, View parent) throws AttrApplyException {
        paramsLazyCreator.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        paramsLazyCreator.width = LinearLayout.LayoutParams.MATCH_PARENT;
    }
}
