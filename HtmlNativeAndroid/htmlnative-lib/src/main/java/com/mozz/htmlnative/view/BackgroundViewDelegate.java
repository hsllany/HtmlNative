package com.mozz.htmlnative.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mozz.htmlnative.css.Background;
import com.mozz.htmlnative.utils.BitmapUtils;

import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/3/24.
 */

public final class BackgroundViewDelegate {
    private View mView;
    private Matrix mTransformMatrix;
    private int mColor = Color.WHITE;
    private Background mBackground;

    public BackgroundViewDelegate(View v, Matrix matrix, int color, Background background) {
        mView = v;
        mTransformMatrix = matrix;
        mColor = color;
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
            float width = bitmap.getWidth();
            float height = bitmap.getHeight();

            if (mBackground != null) {
                if (mBackground.isWidthSet()) {
                    if (mBackground.getWidthMode() == Background.LENGTH) {
                        width = mBackground.getWidth();
                    } else {
                        // FIXME: 17/5/8 如何计算出正确的高宽？
                    }
                }

                if (mBackground.isHeightSet()) {
                    if (mBackground.getHeightMode() == Background.LENGTH) {
                        height = mBackground.getHeight();
                    } else {
                        // FIXME: 17/5/8 如何计算出正确的高宽？
                    }
                }
            }

            Log.d("Background", "width=" + width + ", height=" + height);

            BitmapUtils.process(bitmap, new BackgroundProcessTask(mTransformMatrix, mView,
                    mColor, (int) width, (int) height));
        }
    }

    private static class BackgroundProcessTask implements BitmapUtils.ProcessTask {

        private WeakReference<View> viewRef;
        private Matrix matrix;
        private int color;
        private int width;
        private int height;

        BackgroundProcessTask(Matrix matrix, View view, int color, int width, int height) {
            this.viewRef = new WeakReference<>(view);
            this.matrix = matrix;
            this.color = color;
            this.width = width;
            this.height = height;
        }

        @Override
        public Bitmap process(Bitmap raw) {
            Bitmap newBitmap = raw;
            if (matrix != null) {
                newBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(newBitmap);
                if (this.color != Color.TRANSPARENT) {
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
