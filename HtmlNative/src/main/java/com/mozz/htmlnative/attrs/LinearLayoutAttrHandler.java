package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

class LinearLayoutAttrHandler extends AttrHandler {
    @Override
    public void apply(Context context, String tag, View v, String params, Object value,
                      CharSequence innerElement, ViewGroup.LayoutParams layoutParams, View
                                  parent, boolean isParent) throws AttrApplyException {

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
    public void setDefault(Context context, String tag, View v, CharSequence innerElement,
                           ViewGroup.LayoutParams layoutParams, View parent) throws
            AttrApplyException {
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
    }
}
