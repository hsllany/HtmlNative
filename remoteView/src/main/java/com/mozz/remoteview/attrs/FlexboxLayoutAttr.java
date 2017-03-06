package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.remoteview.AttrApplyException;
import com.mozz.remoteview.RVDomTree;

/**
 * @author Yang Tao, 17/3/3.
 */

public class FlexboxLayoutAttr implements LayoutAttr {
    @Override
    public void apply(Context context, View v, String params, Object value, RVDomTree tree)
            throws AttrApplyException {
        FlexboxLayout flexboxLayout = (FlexboxLayout) v;

        if (params.equals("direction")) {
            String val = value.toString();
            flexboxLayout.setFlexDirection(parseFlexDirection(val));
        }
    }

    @Override
    public void applyToChild(Context context, View v, String params, Object value) {

    }

    private static int parseFlexDirection(String direction) {
        if (direction.equals("column_reverse")) {
            return FlexboxLayout.FLEX_DIRECTION_COLUMN_REVERSE;
        } else if (direction.equals("row_reverse")) {
            return FlexboxLayout.FLEX_DIRECTION_ROW_REVERSE;
        } else if (direction.equals("column")) {
            return FlexboxLayout.FLEX_DIRECTION_COLUMN;
        } else {
            return FlexboxLayout.FLEX_DIRECTION_ROW;
        }
    }
}
