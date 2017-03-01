package com.mozz.remoteview.attrs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mozz.remoteview.common.MainHandler;

public class ImageViewAttr implements Attr {

    @Override
    public void apply(final Context context, final View v, String params, final Object value) {
        if (params.equals("src")) {
            MainHandler.instance().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load(value.toString()).into((ImageView) v);
                }
            });
        }
    }
}
