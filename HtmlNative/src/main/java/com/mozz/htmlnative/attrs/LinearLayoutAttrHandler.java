package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.mozz.htmlnative.AttrApplyException;

public class LinearLayoutAttrHandler extends AttrHandler {
    @Override
    public void apply(Context context, java.lang.String tag, View v, String params, Object value,
                      CharSequence innerElement) throws AttrApplyException {

        LinearLayout l = (LinearLayout) v;

        switch (params) {
            case "text-align":
                if (value.toString().equals("center")) {
                    l.setGravity(Gravity.CENTER);
                } else if (value.toString().equals("left")) {
                    l.setGravity(Gravity.START);
                } else if (value.toString().equals("right")) {
                    l.setGravity(Gravity.END);
                }
                break;

        }
    }

    @Override
    public void setDefault(Context context, String tag, View v, CharSequence innerElement) throws
            AttrApplyException {
        ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);
    }
}
