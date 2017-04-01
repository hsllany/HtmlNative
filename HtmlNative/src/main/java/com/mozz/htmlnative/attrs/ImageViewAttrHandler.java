package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.view.ViewImageAdapter;

public class ImageViewAttrHandler extends AttrHandler {

    @Override
    public void apply(final Context context, java.lang.String tag, final View v, @NonNull java
            .lang.String params, @NonNull final Object value, CharSequence innerElement) throws
            AttrApplyException {
        if (params.equals("src") && HNRenderer.getImageViewAdpater() != null) {
            HNRenderer.getImageViewAdpater().setImage(value.toString(), new ViewImageAdapter(v));
        }
    }
}
