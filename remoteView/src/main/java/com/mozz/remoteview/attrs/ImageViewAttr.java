package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mozz.remoteview.AttrApplyException;
import com.mozz.remoteview.RVDomTree;

public class ImageViewAttr implements Attr {

    @Override
    public void apply(final Context context, final View v, String params, final Object value,
                      RVDomTree tree)
            throws AttrApplyException {
        if (params.equals("src")) {
            Glide.with(context).load(value.toString()).into((ImageView) v);
        }
    }
}
