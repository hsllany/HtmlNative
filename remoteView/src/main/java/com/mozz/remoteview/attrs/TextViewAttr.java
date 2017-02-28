package com.mozz.remoteview.attrs;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class TextViewAttr implements Attr {
    @Override
    public void apply(Context context, View v, String params, Object value) {
        TextView textView = (TextView) v;
        if (params.equals("textColor")) {
            try {
                int color = Color.parseColor(value.toString());
                textView.setTextColor(color);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } else if (params.equals("text")) {
            textView.setText(value.toString());
        }
    }
}
