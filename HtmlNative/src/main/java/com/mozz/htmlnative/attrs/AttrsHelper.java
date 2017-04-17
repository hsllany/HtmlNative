package com.mozz.htmlnative.attrs;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.mozz.htmlnative.ViewRelations;

/**
 * @author Yang Tao, 17/3/22.
 */

public final class AttrsHelper {

    private AttrsHelper() {
    }

    @Nullable
    public static AttrHandler getExtraAttrFromView(@NonNull Class<? extends View> clazz) {
        return ViewRelations.findAttrFromExtraByTag(clazz.getName());
    }

    public static void setPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(padding, padding, padding, padding);
        }
    }

    public static void setPadding(View view, int topBottomPadding, int leftRightPadding) {
        if (view != null) {
            view.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding);
        }
    }

    public static void setPadding(View view, int top, int left, int bottom, int right) {
        if (view != null) {
            view.setPadding(left, top, right, bottom);
        }
    }

    public static void setTopPadding(View v, int top) {
        if (v != null) {
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
        }
    }

    public static void setLeftPadding(View v, int left) {
        if (v != null) {
            v.setPadding(left, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
        }
    }

    public static void setRightPadding(View v, int right) {
        if (v != null) {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), right, v.getPaddingBottom());
        }
    }

    public static void setBottomPadding(View v, int bottom) {
        if (v != null) {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottom);
        }
    }

    public static void setBold(TextView textView) {
        if (textView != null) {
            TextPaint paint = textView.getPaint();
            paint.setFakeBoldText(true);
        }
    }

    public static void setUnderLine(TextView textView) {
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }
}
