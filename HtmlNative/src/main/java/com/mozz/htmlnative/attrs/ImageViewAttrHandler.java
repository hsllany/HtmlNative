package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.view.ViewImageAdapter;

public class ImageViewAttrHandler extends AttrHandler {

    @Override
    public void apply(Context context, String tag, View v, String params, Object value,
                      CharSequence innerElement, ViewGroup.LayoutParams layoutParams, View
                                  parent, boolean isParent) throws AttrApplyException {
        if (params.equals("src") && HNRenderer.getImageViewAdpater() != null && !isParent) {
            HNRenderer.getImageViewAdpater().setImage(value.toString(), new ViewImageAdapter(v));
        }
    }

    @Override
    public void setDefault(Context context, String tag, View v, CharSequence innerElement,
                           ViewGroup.LayoutParams layoutParams, View parent) throws
            AttrApplyException {
        super.setDefault(context, tag, v, innerElement, layoutParams, parent);

        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }
}
