package com.mozz.htmlnative.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

/**
 * @author Yang Tao, 17/3/24.
 */

public class ViewImageAdapter {
    private View mView;

    public ViewImageAdapter(View v) {
        mView = v;
    }

    public void setImage(Bitmap bitmap) {
        if (mView instanceof ImageView) {
            ImageView imageView = (ImageView) mView;
            imageView.setAdjustViewBounds(true);
            imageView.setMaxWidth(5000);
            imageView.setImageBitmap(bitmap);
            imageView.requestLayout();
        } else {
            mView.setBackground(new BitmapDrawable(bitmap));
        }
    }
}
