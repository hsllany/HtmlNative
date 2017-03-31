package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.mozz.htmlnative.attrs.AttrHandler;
import com.mozz.htmlnative.attrs.LayoutAttrHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangTao7
 *         NOT THREAD SAFE
 */

public final class AttrsSet {

    private static final String TAG = AttrsSet.class.getSimpleName();

    @NonNull
    private static Map<Class<? extends View>, AttrHandler> sCachedAttrs = new HashMap<>();

    private Object[] mAttrs;
    private int[] mLength;
    private int mGrowLength;
    private int mLastGrowLength = -1;
    private int mCompacity;

    AttrsSet() {
        this(10);
    }

    AttrsSet(int initCompacity) {
        mAttrs = new Object[initCompacity << 1];
        mLength = new int[initCompacity];
        mGrowLength = 0;
        mCompacity = initCompacity;
    }

    void put(@NonNull AttrsOwner tree, String paramsKey, @NonNull Object value) {
        int startPosition = tree.attrIndex();

        putInternal(startPosition + mLength[startPosition], paramsKey, value);
        mLength[startPosition]++;
    }

    private void putInternal(int position, String paramsKey, Object value) {
        if (position >= mCompacity) {
            grow(mCompacity);
        }

        mAttrs[position << 1] = paramsKey;
        mAttrs[(position << 1) + 1] = value;
        mGrowLength++;
    }

    private void grow(int growSize) {
        if (growSize > 0) {
            Object[] temp = mAttrs;
            int[] tempL = mLength;

            mAttrs = new Object[(mCompacity + growSize) << 1];
            mLength = new int[mCompacity + growSize];

            System.arraycopy(temp, 0, mAttrs, 0, mCompacity << 1);
            System.arraycopy(tempL, 0, mLength, 0, mCompacity);

            mCompacity += growSize;
        }
    }

    void newAttr(@NonNull AttrsOwner tree) {
        if (mLastGrowLength == mGrowLength) {
            mGrowLength++;
        }

        if (mGrowLength >= mCompacity) {
            grow(mCompacity);
        }

        tree.setAttrIndex(mGrowLength);
        mLastGrowLength = mGrowLength;
    }

    @Override
    public String toString() {
        return Arrays.toString(mAttrs);
    }

    public String toString(@NonNull AttrsOwner tree) {
        int startPos = tree.attrIndex();
        int length = mLength[startPos];

        Object[] objects = new Object[length << 1];
        System.arraycopy(mAttrs, startPos << 1, objects, 0, length << 1);

        return Arrays.toString(objects);
    }

    /**
     * apply the attr to the view
     *
     * @param context        {@link android.content.Context}
     * @param sandBoxContext {@link HNSandBoxContext}
     * @param v              {@link android.view.View}
     * @param tree           {@link HNDomTree}
     * @param parent         {@link android.view.ViewGroup}, parent of the view
     * @param layoutParams   {@link android.view.ViewGroup.LayoutParams}, layoutParams for parent
     *                       when add this view to parent
     * @throws AttrApplyException
     */
    public void apply(Context context, @NonNull final HNSandBoxContext sandBoxContext, View v,
                      @NonNull AttrsOwner tree, String innerText, String tagName, @NonNull
                              ViewGroup parent, @NonNull ViewGroup.LayoutParams layoutParams,
                      CssIdClass outCssIdClass) throws AttrApplyException {


        int startPosition = tree.attrIndex();
        int treeAttrLength = mLength[startPosition];

        // clear the value in result
        outCssIdClass.clazz = null;
        outCssIdClass.id = null;

        AttrHandler viewAttrHandler = getAttr(v.getClass());
        AttrHandler extraAttrHandler = AttrsHelper.getExtraAttrFromView(v.getClass());
        AttrHandler parentAttrHandler = getAttr(parent.getClass());
        LayoutAttrHandler parentLayoutAttr = null;
        if (parentAttrHandler instanceof LayoutAttrHandler) {
            parentLayoutAttr = (LayoutAttrHandler) parentAttrHandler;
        }

        // Apply the default attr to view first;
        // Then process each parameter.
        applyDefaultStyle(context, tagName, sandBoxContext, v, innerText, parent, layoutParams,
                viewAttrHandler, extraAttrHandler, parentLayoutAttr);

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            Styles.applyStyle(context, tagName, sandBoxContext, v, (String) mAttrs[i << 1],
                    mAttrs[(i << 1) + 1], innerText, parent, layoutParams, viewAttrHandler,
                    extraAttrHandler, parentLayoutAttr, outCssIdClass);
        }
    }

    public static AttrHandler getViewAttr(View v) {
        return getAttr(v.getClass());
    }

    public static AttrHandler getExtraAttr(View v) {
        return AttrsHelper.getExtraAttrFromView(v.getClass());
    }

    public static LayoutAttrHandler getParentAttr(ViewParent parent) {
        if (parent instanceof ViewGroup) {
            AttrHandler parentAttrHandler = getAttr(((ViewGroup) parent).getClass());
            LayoutAttrHandler parentLayoutAttr = null;
            if (parentAttrHandler instanceof LayoutAttrHandler) {
                parentLayoutAttr = (LayoutAttrHandler) parentAttrHandler;
            }
            return parentLayoutAttr;
        }
        return null;
    }

    /**
     * Apply a default style to view
     */
    private static void applyDefaultStyle(Context context, String tagName, final HNSandBoxContext
            sandBoxContext, View v, String innerElement, @NonNull ViewGroup parent, @NonNull
            ViewGroup.LayoutParams layoutParams, AttrHandler viewAttrHandler, AttrHandler
            extralAttrHandler, LayoutAttrHandler parentAttr) throws AttrApplyException {
        if (viewAttrHandler != null) {
            viewAttrHandler.setDefault(context, tagName, v, innerElement);
        }

        if (extralAttrHandler != null) {
            extralAttrHandler.setDefault(context, tagName, v, innerElement);
        }

        if (parentAttr != null) {
            parentAttr.setDefaultToChild(context, tagName, v, innerElement);
        }
    }

    final Object getAttr(AttrsOwner owner, String attrName) {
        int startPosition = owner.attrIndex();
        int treeAttrLength = mLength[startPosition];

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            String params = (String) mAttrs[i << 1];
            final Object value = mAttrs[(i << 1) + 1];

            if (params.equals(attrName)) {
                return value;
            }
        }

        return null;
    }

    @Nullable
    private static AttrHandler getAttr(@NonNull Class<? extends View> clazz) {
        AttrHandler attrHandler = sCachedAttrs.get(clazz);
        if (attrHandler == null) {
            attrHandler = AttrsHelper.getAttrFromView(clazz);
            if (attrHandler != null) {
                sCachedAttrs.put(clazz, attrHandler);
            }
        }

        return attrHandler;
    }

}
