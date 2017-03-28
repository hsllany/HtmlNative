package com.mozz.htmlnative.css;

import com.mozz.htmlnative.AttrsOwner;

/**
 * @author Yang Tao, 17/3/27.
 */

public abstract class CssSelector implements AttrsOwner {

    protected CssSelector mNext;
    protected int mChainLength;
    protected CssSelector mRoot = this;

    private int mAttrIndex;

    public final void chain(CssSelector selector) {
        selector.mRoot = this.mRoot;
        if (mNext == null) {
            mNext = selector;
        } else {
            mNext.mNext = selector;
            mNext = mNext.mNext;
        }

        mChainLength++;
    }

    public final boolean matchAll(String type, String id, String clazz) {
        CssSelector p = this;
        while (p != null) {
            if (!p.matchThis(type, id, clazz)) {
                return false;
            }

            p = p.mNext;
        }

        return true;
    }

    public abstract boolean matchThis(String type, String id, String clazz);

    public abstract String selfToString();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        CssSelector p = this;
        while (p != null) {
            sb.append(p.selfToString());
            sb.append("->");
            p = p.mNext;
        }
        return sb.toString();
    }

    public final CssSelector next() {
        return mNext;
    }

    @Override
    public int attrIndex() {
        if (mRoot == this) {
            return mAttrIndex;
        } else {
            return mRoot.attrIndex();
        }
    }

    @Override
    public void setAttrIndex(int newIndex) {
        mAttrIndex = newIndex;
    }
}
