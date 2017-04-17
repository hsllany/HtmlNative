package com.mozz.htmlnative.attrs;

import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

/**
 * @author Yang Tao, 17/4/17.
 */

public final class AttrsHandlerFactory {

    private static TextViewAttrHandler sText = new TextViewAttrHandler();
    private static ImageViewAttrHandler sImage = new ImageViewAttrHandler();
    private static LinearLayoutAttrHandler sLinear = new LinearLayoutAttrHandler();
    private static FlexBoxLayoutAttrHandler sFlex = new FlexBoxLayoutAttrHandler();
    private static WebViewAttrHandler sWebview = new WebViewAttrHandler();
    private static AbsoluteAttrHandler sAbsolute = new AbsoluteAttrHandler();

    private AttrsHandlerFactory() {

    }

    //TODO there is much can be done when dealing with the AttrHandler
    public static AttrHandler getAttrFromView(@NonNull Class<? extends View> clazz) {

        if (TextView.class.isAssignableFrom(clazz)) {
            return sText;
        } else if (ImageView.class.isAssignableFrom(clazz)) {
            return sImage;
        } else if (clazz.equals(LinearLayout.class)) {
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
}
