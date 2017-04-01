package com.mozz.htmlnative.attrs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
}
