package com.mozz.htmlnative.attrshandler;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Tao, 17/3/22.
 */

public final class AttrsHelper {

    private AttrsHelper() {
    }

    @NonNull
    private static Map<Class<? extends View>, AttrHandler> sAttrHandlerCache = new HashMap<>();
    private static Map<Class<? extends View>, AttrHandler> sExtraAttrHandlerCache = new HashMap<>();

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

    @Nullable
    public static AttrHandler getAttrHandler(View view) {
        Class<? extends View> vClazz = view.getClass();
        AttrHandler attrHandler = sAttrHandlerCache.get(vClazz);
        if (attrHandler == null) {
            attrHandler = AttrsHandlerFactory.getAttrHandlerFromView(vClazz);
            if (attrHandler != null) {
                sAttrHandlerCache.put(vClazz, attrHandler);
            }
        }

        return attrHandler;

    }

    public static LayoutAttrHandler getParentAttrHandler(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            AttrHandler parentAttrHandler = AttrsHelper.getAttrHandler((View) parent);
            LayoutAttrHandler parentLayoutAttr = null;
            if (parentAttrHandler instanceof LayoutAttrHandler) {
                parentLayoutAttr = (LayoutAttrHandler) parentAttrHandler;
            }
            return parentLayoutAttr;
        } else {
            return null;
        }
    }

    @Nullable
    public static AttrHandler getExtraAttrHandler(View view) {
        return sExtraAttrHandlerCache.get(view.getClass());
    }

    public static AttrHandler registerExtraAttrHandler(Class<? extends View> viewClass,
                                                       AttrHandler attrHandler) {
        return sExtraAttrHandlerCache.put(viewClass, attrHandler);
    }

    public static void clear() {
        sExtraAttrHandlerCache.clear();
        sAttrHandlerCache.clear();
    }
}
