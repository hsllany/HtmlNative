package com.mozz.htmlnative.stylehandler;

import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

/**
 * @author Yang Tao, 17/3/22.
 */

public final class StyleHelper {

    private StyleHelper() {
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
