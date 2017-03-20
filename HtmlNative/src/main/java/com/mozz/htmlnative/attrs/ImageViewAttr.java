package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.mozz.htmlnative.AttrApplyException;
import com.mozz.htmlnative.HNDomElement;
import com.mozz.htmlnative.HNRenderer;

public class ImageViewAttr implements Attr {

    @Override
    public void apply(final Context context, String tag, final View v, @NonNull String params,
                      @NonNull final Object value, HNDomElement tree) throws AttrApplyException {
        if (params.equals("src") && HNRenderer.getImageViewAdpater() != null) {
            HNRenderer.getImageViewAdpater().setImage(value.toString(), (ImageView) v);
        }
    }
}
