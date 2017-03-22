package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.htmlnative.attrs.Attr;
import com.mozz.htmlnative.attrs.FlexBoxLayoutAttr;
import com.mozz.htmlnative.attrs.ImageViewAttr;
import com.mozz.htmlnative.attrs.LinearLayoutAttr;
import com.mozz.htmlnative.attrs.TextViewAttr;
import com.mozz.htmlnative.attrs.WebViewAttr;

/**
 * @author Yang Tao, 17/3/22.
 */

class AttrsHelper {

    private AttrsHelper() {
    }

    //TODO there is much can be done when dealing with the Attr
    static Attr getAttrFromView(@NonNull Class<? extends View> clazz) {
        // cover all TextView sub classes
        if (TextView.class.isAssignableFrom(clazz)) {
            return TextViewAttr.getInstance();

        } else if (clazz.equals(ImageView.class)) {
            return new ImageViewAttr();
        } else if (clazz.equals(LinearLayout.class)) {
            return new LinearLayoutAttr();
        } else if (clazz.equals(FlexboxLayout.class)) {
            return new FlexBoxLayoutAttr();
        } else if (clazz.equals(WebView.class)) {
            return new WebViewAttr();
        } else {
            return null;
        }
    }

    @Nullable
    static Attr getExtraAttrFromView(@NonNull Class<? extends View> clazz) {
        return ViewTagLookupTable.findAttrFromExtraByTag(clazz.getName());
    }
}
