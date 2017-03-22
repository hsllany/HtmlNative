package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.mozz.htmlnative.AttrApplyException;
import com.mozz.htmlnative.HNRenderer;

public class ImageViewAttr extends Attr {

    @Override
    public void apply(final Context context, java.lang.String tag, final View v, @NonNull java
            .lang.String params,
                      @NonNull final Object value, String innerElement) throws AttrApplyException {
        if (params.equals("src") && HNRenderer.getImageViewAdpater() != null) {
            HNRenderer.getImageViewAdpater().setImage(value.toString(), (ImageView) v);
        }
    }
}
