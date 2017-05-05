package com.mozz.htmlnative.css;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.InheritStyleStack;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.attrs.AttrHandler;
import com.mozz.htmlnative.attrs.LayoutAttrHandler;

import java.util.Arrays;

/**
 * @author YangTao7
 *         NOT THREAD SAFE
 */

public class AttrsSet {

    private static final String TAG = AttrsSet.class.getSimpleName();


    private Object[] mAttrs;
    private int[] mLength;
    private int mGrowLength;
    private int mLastGrowLength = -1;
    private int mCompacity;
    private String mName;

    public AttrsSet(String name) {
        this(name, 10);
    }

    public AttrsSet(String name, int initCompacity) {
        mAttrs = new Object[initCompacity << 1];
        mLength = new int[initCompacity];
        mGrowLength = 0;
        mCompacity = initCompacity;
        mName = name;
    }

    public void put(@NonNull AttrsOwner tree, String paramsKey, @NonNull Object value) {
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

    public void newAttr(@NonNull AttrsOwner tree) {
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
     * @param context           {@link Context}
     * @param sandBoxContext    {@link HNSandBoxContext}
     * @param v                 {@link View}
     * @param tree              {@link AttrsOwner}
     * @param parent            {@link ViewGroup}, parent of the view
     * @param layoutParams      {@link ViewGroup.LayoutParams}, layoutParams for parent
     *                          when add this view to parent
     * @param viewAttrHandler
     * @param extraAttrHandler
     * @param parentAttrHandler @throws AttrApplyException
     */
    public void apply(Context context, @NonNull final HNSandBoxContext sandBoxContext, View v,
                      @NonNull AttrsOwner tree, DomElement domElement, @NonNull ViewGroup parent,
                      @NonNull ViewGroup.LayoutParams layoutParams, boolean applyDefault, boolean
                              isParent, AttrHandler viewAttrHandler, AttrHandler
                              extraAttrHandler, LayoutAttrHandler parentAttrHandler,
                      InheritStyleStack stack) throws AttrApplyException {

        int startPosition = tree.attrIndex();
        int treeAttrLength = mLength[startPosition];

        HNLog.d(HNLog.ATTR, "[" + mName + "]: apply to AttrsOwner " + tree.attrIndex() + ", to "
                + domElement.getType());


        // Apply the default attr to view first;
        // Then process each parameter.
        if (applyDefault) {
            applyDefaultStyle(context, sandBoxContext, v, domElement, parent, viewAttrHandler,
                    extraAttrHandler, parentAttrHandler, layoutParams);
        }

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            String param = (String) mAttrs[i << 1];
            Object value = mAttrs[(i << 1) + 1];

            Styles.applyStyle(context, sandBoxContext, v, domElement, layoutParams, parent,
                    viewAttrHandler, extraAttrHandler, parentAttrHandler, param, value, isParent,
                    stack);
        }
    }

    /**
     * Apply a default style to view
     */
    private static void applyDefaultStyle(Context context, final HNSandBoxContext sandBoxContext,
                                          View v, DomElement domElement, @NonNull ViewGroup
                                                  parent, AttrHandler viewAttrHandler,
                                          AttrHandler extralAttrHandler, LayoutAttrHandler
                                                  parentAttr, @NonNull ViewGroup.LayoutParams
                                                  layoutParams) throws AttrApplyException {
        if (viewAttrHandler != null) {
            viewAttrHandler.setDefault(context, v, domElement, layoutParams, parent);
        }

        if (extralAttrHandler != null) {
            extralAttrHandler.setDefault(context, v, domElement, layoutParams, parent);
        }

        if (parentAttr != null) {
            parentAttr.setDefaultToChild(context, v, domElement, parent, layoutParams);
        }
    }

    public final Object getAttr(AttrsOwner owner, String attrName) {
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


    /**
     * @author Yang Tao, 17/3/27.
     */

    public interface AttrsOwner {

        int attrIndex();

        void setAttrIndex(int newIndex);
    }
}
