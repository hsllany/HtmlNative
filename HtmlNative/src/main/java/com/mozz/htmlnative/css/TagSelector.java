package com.mozz.htmlnative.css;

/**
 * @author Yang Tao, 17/3/27.
 */

public class TagSelector extends CssSelector {

    protected String mTag;

    public TagSelector(String tag) {
        mTag = tag;
    }

    @Override
    public boolean matchThis(Object object) {
        return mTag.equals(object.toString());
    }

    @Override
    public String selfToString() {
        return "[Tag=" + mTag + "]";
    }

    @Override
    public int hashCode() {
        return mTag.hashCode();
    }
}
