package com.mozz.remoteview.parser;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Yang Tao on 17/2/22.
 */

public class ImageViewAttr implements Attr {

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void apply(final Context context, final View v, String params, final Object value) {
        if (params.equals("src")) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load(value.toString()).into((ImageView) v);
                }
            });

        }
    }
}
