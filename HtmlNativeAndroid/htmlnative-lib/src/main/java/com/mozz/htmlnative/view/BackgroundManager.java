package com.mozz.htmlnative.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.css.Background;

/**
 * @author Yang Tao, 17/5/9.
 */

class BackgroundManager implements IBackgroundManager {

    private static final String TAG = BackgroundManager.class.getSimpleName();

    private View mHost;

    private Rect mRect = new Rect();
    private Paint mPaint = new Paint();
    private Bitmap mBackgroundBitmap;
    private int mLeft, mTop, mWidth, mHeight;
    private int mColorLeft, mColorTop, mColorWidth, mColorHeight;
    private int mColor = Color.TRANSPARENT;
    private Background mBackground;
    private int mSetBackgroundCount, mMeasureBackgroundCount;

    public BackgroundManager(View hostView) {
        mHost = hostView;
    }

    @Override
    public void setHtmlBackground(Bitmap bitmap, Background background) {
        if (mHost instanceof ViewGroup) {
            mHost.setWillNotDraw(false);
        }
        mBackgroundBitmap = bitmap;
        mColor = background.getColor();
        mBackground = background;
        mSetBackgroundCount++;

        mHost.invalidate();
    }

    @Override
    public Background getHtmlBackground() {
        return mBackground;
    }

    /**
     * Should be called at last in {@link View#onDraw(Canvas)}
     *
     * @param canvas, see {@link View#onDraw(Canvas)}
     */
    public void onDraw(Canvas canvas) {
        measuredBackground();
        if (mBackground != null && mBackground.isColorSet()) {
            mPaint.setColor(mColor);
            canvas.drawRect(mColorLeft, mColorTop, mColorLeft + mColorWidth, mColorTop +
                    mColorHeight, mPaint);

        }

        if (mBackgroundBitmap != null) {
            mRect.set(mLeft, mTop, mLeft + mWidth, mTop + mHeight);
            canvas.drawBitmap(mBackgroundBitmap, null, mRect, null);
        }
    }

    private void measuredBackground() {
        if (mMeasureBackgroundCount == mSetBackgroundCount) {
            return;
        }

        if (mBackground == null) {
            return;
        }

        if (mBackground.getXMode() == Background.LENGTH) {
            mLeft = (int) mBackground.getX();
        } else {
            mLeft = (int) (mBackground.getX() * mHost.getMeasuredWidth());
        }

        if (mBackground.getYMode() == Background.LENGTH) {
            mTop = (int) mBackground.getY();
        } else {
            mTop = (int) (mBackground.getY() * mHost.getMeasuredHeight());
        }

        if (mBackground.getWidthMode() == Background.LENGTH) {
            mWidth = (int) mBackground.getWidth();
        } else if (mBackground.getWidthMode() == Background.AUTO && mBackgroundBitmap != null) {
            mWidth = mBackgroundBitmap.getWidth();
        } else if (mBackground.getWidthMode() == Background.PERCENTAGE) {
            mWidth = (int) (mBackground.getWidth() * mHost.getMeasuredWidth());
        } else {
            mWidth = mHost.getMeasuredWidth();
        }

        if (mBackground.getHeightMode() == Background.LENGTH) {
            mHeight = (int) mBackground.getHeight();
        } else if (mBackground.getHeightMode() == Background.AUTO && mBackgroundBitmap != null) {
            mHeight = mBackgroundBitmap.getHeight();
        } else if (mBackground.getHeightMode() == Background.PERCENTAGE) {
            mHeight = (int) (mBackground.getHeight() * mHost.getMeasuredHeight());
        } else {
            mHeight = mHost.getMeasuredHeight();
        }

        if (mBackground.getColorWidthMode() == Background.LENGTH) {
            mColorWidth = (int) mBackground.getColorWidth();
        } else if (mBackground.getColorWidthMode() == Background.PERCENTAGE) {
            mColorWidth = (int) (mBackground.getColorWidth() * mHost.getMeasuredWidth());
        } else {
            mColorWidth = mHost.getMeasuredWidth();
        }

        if (mBackground.getColorHeightMode() == Background.LENGTH) {
            mColorHeight = (int) mBackground.getColorHeight();
        } else if (mBackground.getColorHeightMode() == Background.PERCENTAGE) {
            mColorHeight = (int) (mBackground.getColorHeight() * mHost.getMeasuredWidth());
        } else {
            mColorHeight = mHost.getMeasuredWidth();
        }

        Log.d(TAG, "CalculateResult: mLeft=" + mLeft + ", mTop=" + mTop + ", mWidth=" + mWidth +
                ", mHeight=" + mHeight + ", mBackground=" + mBackground + ", mColorWidth=" +
                mColorWidth + ", mColorHeight=" + mColorHeight);

        mMeasureBackgroundCount++;
    }
}
