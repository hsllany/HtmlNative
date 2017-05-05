package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;

class HtmlLayoutAttrHandler extends AttrHandler {
    @Override
    public void apply(Context context, View v, DomElement domElement, View parent, ViewGroup
            .LayoutParams layoutParams, String params, Object value, boolean isParent) throws
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
    public void setDefault(Context context, View v, DomElement domElement, ViewGroup.LayoutParams
            layoutParams, View parent) throws AttrApplyException {
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
    }
}
