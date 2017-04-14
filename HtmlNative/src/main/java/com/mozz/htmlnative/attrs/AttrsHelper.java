package com.mozz.htmlnative.attrs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.htmlnative.ViewTagLookupTable;

/**
 * @author Yang Tao, 17/3/22.
 */

public class AttrsHelper {

    private AttrsHelper() {
    }

    //TODO there is much can be done when dealing with the AttrHandler
    public static AttrHandler getAttrFromView(@NonNull Class<? extends View> clazz) {
        // cover all TextView sub classes
        if (TextView.class.isAssignableFrom(clazz)) {
            return TextViewAttrHandler.getInstance();

        } else if (clazz.equals(ImageView.class)) {
            return new ImageViewAttrHandler();
        } else if (clazz.equals(LinearLayout.class)) {
            return new LinearLayoutAttrHandler();
        } else if (clazz.equals(FlexboxLayout.class)) {
            return new FlexBoxLayoutAttrHandler();
        } else if (clazz.equals(WebView.class)) {
            return new WebViewAttrHandler();
        } else {
            return null;
        }
    }

    @Nullable
    public static AttrHandler getExtraAttrFromView(@NonNull Class<? extends View> clazz) {
        return ViewTagLookupTable.findAttrFromExtraByTag(clazz.getName());
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
}
