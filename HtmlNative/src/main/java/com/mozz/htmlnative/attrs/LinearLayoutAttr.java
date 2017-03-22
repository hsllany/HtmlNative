package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.mozz.htmlnative.AttrApplyException;

public class LinearLayoutAttr extends Attr {
    @Override
    public void apply(Context context, java.lang.String tag, View v, String params, Object value,
                      String innerElement) throws AttrApplyException {

    }

    @Override
    public void setDefault(Context context, String tag, View v, String innerElement) throws
            AttrApplyException {
        ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);
    }
}
