package com.mozz.htmlnative.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.google.android.flexbox.FlexboxLayout;

/**
 * @author Yang Tao, 17/5/12.
 */

public class LayoutParamsLazyCreator {
    public int width = ViewGroup.LayoutParams.WRAP_CONTENT;
    public int height = ViewGroup.LayoutParams.WRAP_CONTENT;
    public int marginLeft;
    public int marginTop;
    public int marginRight;
    public int marginBottom;
    public int left;
    public int top;

    public LayoutParamsLazyCreator() {

    }

    public LayoutParamsLazyCreator(int width, int height, int marginLeft, int marginTop, int
            marginRight, int marginBottom, int left, int top) {
        this.width = width;
        this.height = height;
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;

        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.left = left;
        this.top = top;
    }


    public void setMargins(int left, int top, int right, int bottom) {
        marginLeft = left;
        marginTop = top;
        marginRight = right;
        marginBottom = bottom;
    }

    public ViewGroup.MarginLayoutParams toMarginLayoutParams() {
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(width,
                height);
        marginLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        return marginLayoutParams;
    }

    public AbsoluteLayout.LayoutParams toAbsoluteLayoutParams() {
        return new AbsoluteLayout.LayoutParams(width, height,
                left, top);
    }

    public FlexboxLayout.LayoutParams toFlexLayoutParams() {
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(width, height);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        return layoutParams;
    }

    @Override
    public String toString() {
        return "width=" + width + ", height=" + height;
    }

    public static ViewGroup.LayoutParams createLayoutParams(View parent, LayoutParamsLazyCreator
            creator) {

        if (parent instanceof AbsoluteLayout) {
            return creator.toAbsoluteLayoutParams();
        } else if (parent instanceof HNDiv || parent instanceof HNRootView) {
            return creator.toMarginLayoutParams();
        } else if (parent instanceof FlexboxLayout) {
            return creator.toFlexLayoutParams();
        } else {
            throw new IllegalArgumentException("can't create related layoutParams, unknown " +
                    "view type " + parent.toString());
        }
    }

    public static void createLayoutParams(LayoutParamsLazyCreator creator, ViewGroup.LayoutParams
            outParams) {
        outParams.height = creator.height;
        outParams.width = creator.width;
        if (outParams instanceof AbsoluteLayout.LayoutParams) {
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) outParams;
            layoutParams.x = creator.left;
            layoutParams.y = creator.top;

        } else if (outParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) outParams).setMargins(creator.marginLeft, creator
                    .marginTop, creator.marginRight, creator.marginBottom);
        } else {
            throw new IllegalArgumentException("can't create related layoutParams, unknown " +
                    "layoutParams type " + outParams.toString());
        }
    }

}
