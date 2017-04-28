package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.DomElement;
import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.view.ViewImageAdapter;

class ImageViewAttrHandler extends AttrHandler {

    @Override
    public void apply(Context context, View v, DomElement domElement, View parent, ViewGroup
            .LayoutParams layoutParams, String params, Object value, boolean isParent) throws
            AttrApplyException {
        if (params.equals("src") && HNRenderer.getImageViewAdpater() != null && !isParent) {
            HNRenderer.getImageViewAdpater().setImage(value.toString(), new ViewImageAdapter(v));
        }
    }

    @Override
    public void setDefault(Context context, View v, DomElement domElement, ViewGroup.LayoutParams
            layoutParams, View parent) throws AttrApplyException {
        super.setDefault(context, v, domElement, layoutParams, parent);

        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }
}
