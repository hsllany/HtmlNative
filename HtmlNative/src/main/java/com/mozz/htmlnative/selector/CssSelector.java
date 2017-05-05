package com.mozz.htmlnative.selector;

import android.support.annotation.NonNull;

import com.mozz.htmlnative.AttrsSet;
import com.mozz.htmlnative.DomElement;

/**
 * @author Yang Tao, 17/3/27.
 */

public abstract class CssSelector implements AttrsSet.AttrsOwner {
    /**
     * chain the selector together which css is a b > c {***}
     */
    private CssSelector mHead = this;
    private CssSelector mTail = this;
    private CssSelector mPre = null;
    private CssSelector mNext = null;


    /**
     * chain the selector together which css is a, b, c {***}
     */
    private CssSelector mGroupTail;
    private CssSelector mGroupNext;

    /**
     * indicator that whether this selector matches all descendant or only direct child. If true,
     * then all the descendant will be matched; false, only direct child will be matched.
     */
    private boolean mMatchDirect = false;


    private int mAttrIndex;

    public final void chainGroup(CssSelector st) {
        st.mAttrIndex = this.mAttrIndex;
        if (mGroupTail == null) {
            mGroupTail = st;
            mGroupNext = st;
        } else {
            mGroupTail.mGroupNext = st;
            mGroupTail = st;
        }
    }

    /**
     * chain css selector st with current css selector
     *
     * @param st,              css selector to be chained
     * @param matchDescendant, whether st matches all descendant or direct child.
     */
    public final void chainChild(@NonNull CssSelector st, boolean matchDescendant) {
        this.mTail.mMatchDirect = !matchDescendant;
        st.mAttrIndex = this.mAttrIndex;
        st.mHead = this.mHead;

        st.mPre = mTail;
        mTail.mNext = st;
        mTail = st;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        CssSelector curChild = this;
        while (curChild != null) {
            sb.append(curChild.selfToString());
            if (curChild.mPre != null) {
                if (curChild.mPre.matchDirectChild()) {
                    sb.append(" < ");
                } else {
                    sb.append(" ");
                }
            }
            curChild = curChild.mPre;
        }

        return sb.toString();
    }

    public abstract boolean matchThis(DomElement element);

    public final boolean matchWhole(DomElement element) {
        return this.matchBackward(element);
    }

    private boolean matchBackward(DomElement element) {
        DomElement curE = element;
        CssSelector curS = this;

        boolean isFirst = true;

        while (true) {
            if (isFirst) {
                if (!curS.matchThis(curE)) {
                    return false;
                }

                curE = curE.getParent();
                curS = curS.mPre;

                isFirst = false;
            } else {

                if (curS.matchThis(curE)) {
                    curS = curS.mPre;
                    curE = curE.getParent();
                } else {
                    if (curS.matchDirectChild()) {
                        curS = curS.mNext;
                    } else {
                        curE = curE.getParent();
                    }
                }
            }

            if (curS == null) {
                return true;
            }

            if (curE == null) {
                return false;
            }

        }
    }

    public abstract String selfToString();


    public final CssSelector nextChild() {
        return mNext;
    }

    public final CssSelector preChild() {
        return mPre;
    }

    public final CssSelector nextGroup() {
        return mGroupNext;
    }

    public final CssSelector tail() {
        return mTail;
    }

    public final CssSelector head() {
        return mHead;
    }

    @Override
    public int attrIndex() {
        return mAttrIndex;
    }

    @Override
    public void setAttrIndex(int newIndex) {
        mAttrIndex = newIndex;
    }

    public boolean matchDescendant() {
        return !mMatchDirect;
    }

    public boolean matchDirectChild() {
        return mMatchDirect;
    }
}
