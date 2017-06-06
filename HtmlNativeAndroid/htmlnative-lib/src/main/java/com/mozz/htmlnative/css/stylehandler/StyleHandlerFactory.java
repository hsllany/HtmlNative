package com.mozz.htmlnative.css.stylehandler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.htmlnative.view.HNDiv;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Tao, 17/4/17.
 */

public final class StyleHandlerFactory {

    private static TextViewStyleHandler sText = new TextViewStyleHandler();
    private static ImageViewStyleHandler sImage = new ImageViewStyleHandler();
    private static HtmlLayoutStyleHandler sLinear = new HtmlLayoutStyleHandler();
    private static FlexBoxLayoutStyleHandler sFlex = new FlexBoxLayoutStyleHandler();
    private static WebViewStyleHandler sWebview = new WebViewStyleHandler();
    private static AbsoluteStyleHandler sAbsolute = new AbsoluteStyleHandler();
    @NonNull
    private static Map<Class<? extends View>, StyleHandler> sAttrHandlerCache = new HashMap<>();
    private static Map<Class<? extends View>, StyleHandler> sExtraAttrHandlerCache = new
            HashMap<>();

    private StyleHandlerFactory() {

    }

    //TODO there is much can be done when dealing with the StyleHandler
    public static StyleHandler byClass(@NonNull Class<? extends View> clazz) {

        if (TextView.class.isAssignableFrom(clazz)) {
            return sText;
        } else if (ImageView.class.isAssignableFrom(clazz)) {
            return sImage;
        } else if (clazz.equals(HNDiv.class)) {
            return sLinear;
        } else if (clazz.equals(FlexboxLayout.class)) {
            return sFlex;
        } else if (clazz.equals(WebView.class)) {
            return sWebview;
        } else if (clazz.equals(AbsoluteLayout.class)) {
            return sAbsolute;
        } else {
            return null;
        }
    }

    @Nullable
    public static StyleHandler get(View view) {
        Class<? extends View> vClazz = view.getClass();
        StyleHandler styleHandler = sAttrHandlerCache.get(vClazz);
        if (styleHandler == null) {
            styleHandler = byClass(vClazz);
            if (styleHandler != null) {
                sAttrHandlerCache.put(vClazz, styleHandler);
            }
        }

        return styleHandler;

    }

    /**
     * Get styleHandler of view's parent
     */
    public static LayoutStyleHandler parentGet(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            StyleHandler parentStyleHandler = get((View) parent);
            LayoutStyleHandler parentLayoutAttr = null;
            if (parentStyleHandler instanceof LayoutStyleHandler) {
                parentLayoutAttr = (LayoutStyleHandler) parentStyleHandler;
            }
            return parentLayoutAttr;
        } else {
            return null;
        }
    }

    /**
     * Get StyleHandler from extra pools
     */
    @Nullable
    public static StyleHandler extraGet(View view) {
        return sExtraAttrHandlerCache.get(view.getClass());
    }

    public static StyleHandler registerExtraStyleHandler(Class<? extends View> viewClass,
                                                         StyleHandler styleHandler) {
        return sExtraAttrHandlerCache.put(viewClass, styleHandler);
    }

    public static void unregisterExtraStyleHandler(Class<? extends View> viewClass) {
        sExtraAttrHandlerCache.remove(viewClass);
    }

    public static void clearExtraStyleHandler() {
        sExtraAttrHandlerCache.clear();
    }

    public static void clear() {
        sExtraAttrHandlerCache.clear();
        sAttrHandlerCache.clear();
    }
}
