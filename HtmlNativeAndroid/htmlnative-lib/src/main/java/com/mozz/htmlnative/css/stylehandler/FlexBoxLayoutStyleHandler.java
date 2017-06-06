package com.mozz.htmlnative.css.stylehandler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsCreator;

/**
 * @author Yang Tao, 17/3/3.
 */

class FlexBoxLayoutStyleHandler extends LayoutStyleHandler {
    @Override
    public void apply(Context context, View v, DomElement domElement, View parent,
                      LayoutParamsCreator paramsCreator, String params, Object value)
            throws AttrApplyException {
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
    public void applyToChild(Context context, View v, DomElement domElement, View parent, LayoutParamsCreator paramsCreator, String params, Object value) throws AttrApplyException {

    }

    @Override
    public void setDefault(Context context, View v, DomElement domElement,
                           LayoutParamsCreator paramsCreator, View parent) throws
            AttrApplyException {
        super.setDefault(context, v, domElement, paramsCreator, parent);

        paramsCreator.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @FlexboxLayout.FlexDirection
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

    @FlexboxLayout.FlexWrap
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

    @FlexboxLayout.JustifyContent
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
