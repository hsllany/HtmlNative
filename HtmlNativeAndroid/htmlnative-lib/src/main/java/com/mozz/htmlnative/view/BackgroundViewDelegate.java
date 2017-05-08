package com.mozz.htmlnative.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.mozz.htmlnative.utils.BitmapUtils;

import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/3/24.
 */

public final class BackgroundViewDelegate {
    private View mView;
    private Matrix mTransformMatrix;
    private int mColor = Color.WHITE;

    public BackgroundViewDelegate(View v, Matrix matrix, int color) {
        mView = v;
        mTransformMatrix = matrix;
        mColor = color;
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
            BitmapUtils.process(bitmap, new BackgroundProcessTask(mTransformMatrix, mView, mColor));
        }
    }

    private static class BackgroundProcessTask implements BitmapUtils.ProcessTask {

        private WeakReference<View> viewRef;
        private Matrix matrix;
        private int color;

        BackgroundProcessTask(Matrix matrix, View view, int color) {
            this.viewRef = new WeakReference<>(view);
            this.matrix = matrix;
            this.color = color;
        }

        @Override
        public Bitmap process(Bitmap raw) {
            Bitmap newBitmap = raw;
            if (matrix != null) {
                newBitmap = Bitmap.createBitmap(raw.getWidth(), raw.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(newBitmap);
                if(this.color != Color.TRANSPARENT) {
                    canvas.drawColor(this.color);
                }
                canvas.drawBitmap(raw, matrix, null);
            }

            return newBitmap;
        }

        @Override
        public void done(Bitmap bitmap) {
            View view = viewRef.get();
            if (view != null) {
                view.setBackground(new BitmapDrawable(view.getResources(), bitmap));
            }
        }
    }
}
