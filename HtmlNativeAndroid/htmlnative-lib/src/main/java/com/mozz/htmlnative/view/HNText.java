package com.mozz.htmlnative.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mozz.htmlnative.css.Background;

/**
 * @author Yang Tao, 17/5/9.
 */
public class HNText extends TextView implements IBackgroundView {
    private BackgroundManager mBackgroundMgr;

    public HNText(Context context) {
        super(context);
        mBackgroundMgr = new BackgroundManager(this);
    }

    public HNText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackgroundMgr = new BackgroundManager(this);
    }

    public HNText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackgroundMgr = new BackgroundManager(this);
    }

    public HNText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mBackgroundMgr = new BackgroundManager(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBackgroundMgr.onDraw(canvas);
        super.onDraw(canvas);
    }


    @Override
    public void setHtmlBackground(Bitmap bitmap, Background background) {
        mBackgroundMgr.setHtmlBackground(bitmap, background);
    }

    @Override
    public Background getHtmlBackground() {
        return mBackgroundMgr.getHtmlBackground();
    }
}
