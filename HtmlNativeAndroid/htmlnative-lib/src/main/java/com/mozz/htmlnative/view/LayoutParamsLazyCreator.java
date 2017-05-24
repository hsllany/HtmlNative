package com.mozz.htmlnative.view;

import android.view.View;
import android.view.ViewGroup;

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
    public int right;
    public int bottom;

    @HNDiv.HNDivLayoutParams.HNDivPosition
    public int positionMode = HNDiv.HNDivLayoutParams.POSITION_STATIC;

    public LayoutParamsLazyCreator() {

    }

    public LayoutParamsLazyCreator(ViewGroup.LayoutParams params) {
        width = params.width;
        height = params.height;

        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginLeft = ((ViewGroup.MarginLayoutParams) params).leftMargin;
            marginTop = ((ViewGroup.MarginLayoutParams) params).topMargin;
            marginRight = ((ViewGroup.MarginLayoutParams) params).rightMargin;
            marginBottom = ((ViewGroup.MarginLayoutParams) params).bottomMargin;
        }

        if (params instanceof HNDiv.HNDivLayoutParams) {
            left = ((HNDiv.HNDivLayoutParams) params).left;
            top = ((HNDiv.HNDivLayoutParams) params).top;
            right = ((HNDiv.HNDivLayoutParams) params).right;
            bottom = ((HNDiv.HNDivLayoutParams) params).bottom;
            positionMode = ((HNDiv.HNDivLayoutParams) params).positionMode;
        }

    }

    public LayoutParamsLazyCreator(int width, int height, int marginLeft, int marginTop, int
            marginRight, int marginBottom, int left, int top, int right, int bottom, @HNDiv
            .HNDivLayoutParams.HNDivPosition int floatMode) {
        this.width = width;
        this.height = height;
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;

        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.positionMode = floatMode;
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

    public FlexboxLayout.LayoutParams toFlexLayoutParams() {
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(width, height);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        return layoutParams;
    }

    public HNDiv.HNDivLayoutParams toHNDivLayoutParams() {
        HNDiv.HNDivLayoutParams hnDivLayoutParams = new HNDiv.HNDivLayoutParams(width, height);
        hnDivLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        hnDivLayoutParams.left = left;
        hnDivLayoutParams.top = top;
        hnDivLayoutParams.right = right;
        hnDivLayoutParams.bottom = bottom;
        hnDivLayoutParams.positionMode = positionMode;
        return hnDivLayoutParams;
    }

    @Override
    public String toString() {
        return "width=" + toString(width) + ", height=" + toString(height);
    }

    public static ViewGroup.LayoutParams createLayoutParams(View parent, LayoutParamsLazyCreator
            creator) {
        if (parent instanceof HNDiv) {
            return creator.toHNDivLayoutParams();
        } else if (parent instanceof HNRootView) {
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
        if (outParams instanceof HNDiv.HNDivLayoutParams) {
            ((HNDiv.HNDivLayoutParams) outParams).setMargins(creator.marginLeft, creator
                    .marginTop, creator.marginRight, creator.marginBottom);
            ((HNDiv.HNDivLayoutParams) outParams).positionMode = creator.positionMode;
            ((HNDiv.HNDivLayoutParams) outParams).left = creator.left;
            ((HNDiv.HNDivLayoutParams) outParams).top = creator.top;
            ((HNDiv.HNDivLayoutParams) outParams).bottom = creator.bottom;
            ((HNDiv.HNDivLayoutParams) outParams).right = creator.right;
        } else if (outParams instanceof FlexboxLayout.LayoutParams) {
            //TODO complete
        } else if (outParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) outParams).setMargins(creator.marginLeft, creator
                    .marginTop, creator.marginRight, creator.marginBottom);

        } else {
            throw new IllegalArgumentException("can't create related layoutParams, unknown " +
                    "layoutParams type " + outParams.toString());
        }
    }

    private static String toString(int dimension) {
        switch (dimension) {
            case ViewGroup.LayoutParams.MATCH_PARENT:
                return "match_parent";
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                return "wrap_content";
            default:
                return dimension + "px";
        }
    }

}
