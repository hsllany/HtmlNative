package com.mozz.htmlnative.css;

import com.mozz.htmlnative.AttrsOwner;

/**
 * @author Yang Tao, 17/3/27.
 */

public abstract class CssSelector implements AttrsOwner {

    protected CssSelector mNext;
    protected int mChainLength;

    private int mAttrIndex;

    public void chain(CssSelector selector) {
        if (mNext == null) {
            mNext = selector;
        } else {
            mNext.mNext = selector;
            mNext = mNext.mNext;
        }

        mChainLength++;
    }

    public boolean matchAll(Object object) {
        CssSelector p = this;
        while (p != null) {
            if (!p.matchThis(object)) {
                return false;
            }

            p = p.mNext;
        }

        return true;
    }

    public abstract boolean matchThis(Object object);

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

    @Override
    public int attrIndex() {
        return mAttrIndex;
    }

    @Override
    public void setAttrIndex(int newIndex) {
        mAttrIndex = newIndex;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public String getInner() {
        return null;
    }
}
