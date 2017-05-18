package com.mozz.htmlnative.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;

import com.mozz.htmlnative.css.Background;

/**
 * @author Yang Tao, 17/3/24.
 */

public final class BackgroundViewDelegate {
    private View mView;
    private Matrix mTransformMatrix;
    private Background mBackground;

    public BackgroundViewDelegate(View v, Matrix matrix, Background background) {
        mView = v;
        mTransformMatrix = matrix;
        mBackground = background;
    }

    public void setBitmap(Bitmap bitmap) {
        if (mView instanceof ImageView) {
            ImageView imageView = (ImageView) mView;
            imageView.setAdjustViewBounds(true);
            if (mTransformMatrix != null) {
                imageView.setImageMatrix(mTransformMatrix);
            }
            imageView.setImageBitmap(bitmap);
        } else {
            if (mView instanceof IBackgroundView) {
                ((IBackgroundManager) mView).setHtmlBackground(bitmap, mBackground);
            }
        }
    }
}
