package com.mozz.htmlnative.css;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Iterator;

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

    public void register(@NonNull AttrsOwner tree) {
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


    public final Iterator<Styles.StyleEntry> iterator(AttrsOwner owner) {
        final int startPosition = owner.attrIndex();
        final int length = mLength[startPosition];

        return new Iterator<Styles.StyleEntry>() {

            private int index = startPosition;
            private int size = length + startPosition;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Styles.StyleEntry next() {
                if (index > size) {
                    return null;
                }

                Styles.StyleEntry styleEntry = new Styles.StyleEntry(getStyleName(index),
                        getStyle(index));
                index++;
                return styleEntry;
            }
        };
    }


    public final Object getStyle(AttrsOwner owner, String styleName) {
        int startPosition = owner.attrIndex();
        int treeAttrLength = mLength[startPosition];

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            String params = (String) mAttrs[i << 1];
            final Object value = mAttrs[(i << 1) + 1];

            if (params.equals(styleName)) {
                return value;
            }
        }

        return null;
    }

    protected final String getStyleName(int pos) {
        return (String) mAttrs[pos << 1];
    }

    protected final Object getStyle(int pos) {
        return mAttrs[(pos << 1) + 1];
    }

    public String getName() {
        return mName;
    }


    /**
     * @author Yang Tao, 17/3/27.
     */

    public interface AttrsOwner {

        int attrIndex();

        void setAttrIndex(int newIndex);
    }
}
