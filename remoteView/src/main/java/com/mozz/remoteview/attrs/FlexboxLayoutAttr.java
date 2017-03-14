package com.mozz.remoteview.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.remoteview.AttrApplyException;
import com.mozz.remoteview.RVDomElement;

/**
 * @author Yang Tao, 17/3/3.
 */

public class FlexBoxLayoutAttr implements LayoutAttr {
    @Override
    public void apply(Context context, String tag, View v, @NonNull String params, @NonNull Object value, RVDomElement tree)
            throws AttrApplyException {
        FlexboxLayout flexboxLayout = (FlexboxLayout) v;

        if (params.equals("flex-direction")) {
            String val = value.toString();
            flexboxLayout.setFlexDirection(flexDirection(val));
        } else if (params.equals("flex-wrap")) {
            String val = value.toString();
            flexboxLayout.setFlexWrap(flexWrap(val));
        } else if (params.equals("justify-content")) {
            String val = value.toString();
            flexboxLayout.setJustifyContent(justContent(val));
        }
    }

    @Override
    public void applyToChild(Context context, String tag, View v, ViewGroup parent, String params, Object value) {

    }

    private static int flexDirection(@NonNull String direction) {
        switch (direction) {
            case "column-reverse":
                return FlexboxLayout.FLEX_DIRECTION_COLUMN_REVERSE;
            case "row-reverse":
                return FlexboxLayout.FLEX_DIRECTION_ROW_REVERSE;
            case "column":
                return FlexboxLayout.FLEX_DIRECTION_COLUMN;
            default:
                return FlexboxLayout.FLEX_DIRECTION_ROW;
        }
    }

    private static int flexWrap(String wrap) {
        switch (wrap) {
            case "nowrap":
                return FlexboxLayout.FLEX_WRAP_NOWRAP;
            case "wrap":
                return FlexboxLayout.FLEX_WRAP_WRAP;
            case "wrap-reverse":
                return FlexboxLayout.FLEX_WRAP_WRAP_REVERSE;
            default:
                return FlexboxLayout.FLEX_WRAP_NOWRAP;
        }
    }

    private static int justContent(String content) {
        switch (content) {
            case "flex-start":
                return FlexboxLayout.JUSTIFY_CONTENT_FLEX_START;
            case "flex-end":
                return FlexboxLayout.JUSTIFY_CONTENT_FLEX_END;
            case "center":
                return FlexboxLayout.JUSTIFY_CONTENT_CENTER;
            case "space-between":
                return FlexboxLayout.JUSTIFY_CONTENT_SPACE_BETWEEN;
            case "space-around":
                return FlexboxLayout.JUSTIFY_CONTENT_SPACE_AROUND;
            default:
                return FlexboxLayout.JUSTIFY_CONTENT_FLEX_START;
        }
    }
}
