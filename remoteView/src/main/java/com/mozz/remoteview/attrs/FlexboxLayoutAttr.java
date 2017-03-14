package com.mozz.remoteview.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.remoteview.AttrApplyException;
import com.mozz.remoteview.RVDomTree;

/**
 * @author Yang Tao, 17/3/3.
 */

public class FlexboxLayoutAttr implements LayoutAttr {
    @Override
    public void apply(Context context, String tag, View v, @NonNull String params, @NonNull Object value, RVDomTree tree)
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
        if (direction.equals("column-reverse")) {
            return FlexboxLayout.FLEX_DIRECTION_COLUMN_REVERSE;
        } else if (direction.equals("row-reverse")) {
            return FlexboxLayout.FLEX_DIRECTION_ROW_REVERSE;
        } else if (direction.equals("column")) {
            return FlexboxLayout.FLEX_DIRECTION_COLUMN;
        } else {
            return FlexboxLayout.FLEX_DIRECTION_ROW;
        }
    }

    private static int flexWrap(String wrap) {
        if (wrap.equals("nowrap")) {
            return FlexboxLayout.FLEX_WRAP_NOWRAP;
        } else if (wrap.equals("wrap")) {
            return FlexboxLayout.FLEX_WRAP_WRAP;
        } else if (wrap.equals("wrap-reverse")) {
            return FlexboxLayout.FLEX_WRAP_WRAP_REVERSE;
        } else {
            return FlexboxLayout.FLEX_WRAP_NOWRAP;
        }
    }

    private static int justContent(String content) {
        if (content.equals("flex-start")) {
            return FlexboxLayout.JUSTIFY_CONTENT_FLEX_START;
        } else if (content.equals("flex-end")) {
            return FlexboxLayout.JUSTIFY_CONTENT_FLEX_END;
        } else if (content.equals("center")) {
            return FlexboxLayout.JUSTIFY_CONTENT_CENTER;
        } else if (content.equals("space-between")) {
            return FlexboxLayout.JUSTIFY_CONTENT_SPACE_BETWEEN;
        } else if (content.equals("space-around")) {
            return FlexboxLayout.JUSTIFY_CONTENT_SPACE_AROUND;
        } else
            return FlexboxLayout.JUSTIFY_CONTENT_FLEX_START;
    }
}
