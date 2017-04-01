package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;

/**
 * @author Yang Tao, 17/3/3.
 */

public class FlexBoxLayoutAttrHandler extends LayoutAttrHandler {
    @Override
    public void apply(Context context, java.lang.String tag, View v, @NonNull java.lang.String
            params, @NonNull Object value, CharSequence innerElement) throws AttrApplyException {
        FlexboxLayout flexboxLayout = (FlexboxLayout) v;

        switch (params) {
            case "flex-direction": {
                String val = value.toString();
                flexboxLayout.setFlexDirection(flexDirection(val));
                break;
            }
            case "flex-wrap": {
                String val = value.toString();
                flexboxLayout.setFlexWrap(flexWrap(val));
                break;
            }
            case "justify-content": {
                String val = value.toString();
                flexboxLayout.setJustifyContent(justContent(val));
                break;
            }
        }
    }

    @Override
    public void applyToChild(Context context, java.lang.String tag, View v, ViewGroup parent,
                             java.lang.String params, Object value) {

    }

    private static int flexDirection(@NonNull java.lang.String direction) {
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

    private static int flexWrap(java.lang.String wrap) {
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

    private static int justContent(java.lang.String content) {
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
