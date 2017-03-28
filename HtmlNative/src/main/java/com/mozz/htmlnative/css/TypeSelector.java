package com.mozz.htmlnative.css;

/**
 * @author Yang Tao, 17/3/27.
 */

public class TypeSelector extends CssSelector {

    protected String mTag;

    public TypeSelector(String tag) {
        mTag = tag;
    }

    public String getName() {
        return mTag;
    }

    @Override
    public boolean matchThis(String type, String id, String clazz) {
        return mTag.equals(type);
    }

    @Override
    public String selfToString() {
        return "[Type=" + mTag + "]";
    }

    @Override
    public int hashCode() {
        return mTag.hashCode();
    }
}
