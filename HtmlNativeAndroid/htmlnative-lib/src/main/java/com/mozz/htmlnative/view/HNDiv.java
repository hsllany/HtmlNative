package com.mozz.htmlnative.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.css.Background;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Yang Tao, 17/4/18.
 */

public class HNDiv extends ViewGroup implements IBackgroundView {

    private static final String TAG = HNDiv.class.getSimpleName();

    private List<List<View>> mAllViews = new ArrayList<>();
    private List<View> mFloatViews = new LinkedList<>();
    private List<Integer> mLineLength = new ArrayList<>();
    private BackgroundManager mBackgroundMgr;
    private Map<String, Object> mSavedInheritStyles = new HashMap<>();

    public HNDiv(Context context) {
        super(context);
        mBackgroundMgr = new BackgroundManager(this);
    }

    public HNDiv(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackgroundMgr = new BackgroundManager(this);
    }

    public HNDiv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackgroundMgr = new BackgroundManager(this);
    }


    public HNDiv(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mBackgroundMgr = new BackgroundManager(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mFloatViews.clear();

        final int msWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int msHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;
        int height = getPaddingLeft();
        int length = getChildCount();
        int lineWidth = getPaddingLeft();
        int lineHeight = 0;
        boolean firstLine = true;
        for (int i = 0; i < length; i++) {
            View child = getChildAt(i);

            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            HNDivLayoutParams lp = (HNDivLayoutParams) child.getLayoutParams();

            if (lp.positionMode == HNDivLayoutParams.POSITION_STATIC || lp.positionMode ==
                    HNDivLayoutParams.POSITION_RELATIVE) {

                int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

                if (childWidth + lineWidth + getPaddingRight() > msWidth) {
                    width = Math.max(lineWidth + getPaddingRight(), childWidth + getPaddingLeft());
                    height += lineHeight;

                    lineWidth = childWidth + getPaddingLeft();
                    lineHeight = childHeight;

                    if (firstLine) {
                        firstLine = false;
                    }
                } else {
                    lineWidth += childWidth;
                    lineHeight = Math.max(lineHeight, childHeight);
                }


            } else if (lp.positionMode == HNDivLayoutParams.POSITION_FLOAT_LEFT || lp
                    .positionMode == HNDivLayoutParams.POSITION_FLOAT_RIGHT) {
                mFloatViews.add(child);
                continue;
            }

        }

        width = Math.max(lineWidth, width);
        height += lineHeight;

        height = height + getPaddingBottom();

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? msWidth : width, heightMode ==
                MeasureSpec.EXACTLY ? msHeight : height);
    }

    @Override
    public void addView(View child, LayoutParams params) {
        if (!(params instanceof HNDivLayoutParams)) {
            super.addView(child, new HNDivLayoutParams(params));
        } else {
            super.addView(child, params);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineLength.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();

        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            HNDivLayoutParams lp = (HNDivLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lp.positionMode == HNDivLayoutParams.POSITION_STATIC || lp.positionMode ==
                    HNDivLayoutParams.POSITION_RELATIVE) {
                if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth + paddingLeft +
                        paddingRight > width) {
                    mLineLength.add(lineHeight);
                    mAllViews.add(lineViews);
                    lineWidth = childWidth;
                    lineViews = new ArrayList<>();
                    lineViews.add(child);
                    lineHeight = childHeight;
                } else {
                    /**
                     * 如果不需要换行，则累加
                     */
                    lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
                    lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
                    lineViews.add(child);
                }
            } else if (lp.positionMode == HNDivLayoutParams.POSITION_ABSOLUTE) {
                child.layout(lp.left, lp.top, lp.left + child.getMeasuredWidth(), lp.top + child
                        .getMeasuredHeight());
            }
        }
        mLineLength.add(lineHeight);
        mAllViews.add(lineViews);

        int left = paddingLeft;
        int top = paddingTop;
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineLength.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                HNDivLayoutParams lp = (HNDivLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                if (lp.positionMode == HNDivLayoutParams.POSITION_STATIC) {
                    child.layout(lc, tc, rc, bc);
                } else {
                    child.layout(lc + lp.left, tc + lp.top, rc, bc);
                }

                left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
            }
            left = paddingLeft;
            top += lineHeight;
        }

        top = paddingTop;
        left = paddingLeft;
        int lastLineHeight = 0;
        int right = getMeasuredWidth() - paddingRight;

        lineWidth = getMeasuredWidth() - paddingLeft - paddingRight;

        for (View v : mFloatViews) {
            if (v.getVisibility() == View.GONE) {
                continue;
            }

            HNDivLayoutParams lp = (HNDivLayoutParams) v.getLayoutParams();

            if (v.getMeasuredWidth() + lp.rightMargin + lp.leftMargin > lineWidth) {
                right = getMeasuredWidth() - paddingRight;
                left = paddingLeft;
                top += lastLineHeight;
                lastLineHeight = 0;
                lineWidth = getMeasuredWidth() - paddingLeft - paddingRight;
            }
            if (lp.positionMode == HNDivLayoutParams.POSITION_FLOAT_LEFT) {
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + v.getMeasuredWidth();
                int bc = tc + v.getMeasuredHeight();
                v.layout(lc, tc, rc, bc);

                left += v.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

            } else {
                int lc = right - lp.rightMargin - v.getMeasuredWidth();
                int tc = top + lp.topMargin;
                int rc = right - lp.rightMargin;
                int bc = tc + v.getMeasuredHeight();

                v.layout(lc, tc, rc, bc);

                right -= v.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            lastLineHeight = Math.max(lastLineHeight, v.getHeight() + lp.topMargin + lp
                    .bottomMargin);
            lineWidth -= v.getMeasuredWidth() - lp.leftMargin - lp.rightMargin;
        }
    }

    public void saveInheritStyles(String styleName, Object style) {
        mSavedInheritStyles.put(styleName, style);
    }

    public Object getInheritStyle(String styleName) {
        return mSavedInheritStyles.get(styleName);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new HNDivLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
    }

    public void setHtmlBackground(Bitmap bitmap, Background background) {
        mBackgroundMgr.setHtmlBackground(bitmap, background);
    }

    @Override
    public void setHtmlBackground(Drawable drawable, Background background) {
        mBackgroundMgr.setHtmlBackground(drawable, background);
    }

    @Override
    public Background getHtmlBackground() {
        return mBackgroundMgr.getHtmlBackground();
    }

    @Override
    public void setBackground(Drawable background) {
        // don't support the background!! Use setHtmlBackground instead
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBackgroundMgr.onDraw(canvas);
        super.onDraw(canvas);

    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public static class HNDivLayoutParams extends MarginLayoutParams {

        @IntDef({POSITION_ABSOLUTE, POSITION_RELATIVE, POSITION_STATIC, POSITION_FLOAT_LEFT,
                POSITION_FLOAT_RIGHT})
        @Retention(RetentionPolicy.SOURCE)
        public @interface HNDivPosition {

        }

        public static final int POSITION_STATIC = 0x01;
        public static final int POSITION_RELATIVE = 0x02;
        public static final int POSITION_ABSOLUTE = 0x03;
        public static final int POSITION_FLOAT_LEFT = 0x04;
        public static final int POSITION_FLOAT_RIGHT = 0x05;

        public int left, top, right, bottom;

        @HNDivPosition
        public int positionMode;

        public HNDivLayoutParams(int width, int height) {
            super(width, height);
            this.positionMode = POSITION_STATIC;
        }

        public HNDivLayoutParams(MarginLayoutParams source) {
            super(source);
            this.positionMode = POSITION_STATIC;
        }

        public HNDivLayoutParams(LayoutParams source) {
            super(source);
            this.positionMode = POSITION_STATIC;
        }

        public HNDivLayoutParams(HNDivLayoutParams source) {
            super(source);

            this.left = source.left;
            this.top = source.top;
            this.right = source.right;
            this.bottom = source.bottom;
            this.positionMode = source.positionMode;
        }
    }
}
