package com.mozz.htmlnative.selector;

import com.mozz.htmlnative.DomElement;

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
    public boolean matchThis(DomElement element) {
        return element.getType().equals(mTag);
    }

    @Override
    public String selfToString() {
        return mTag;
    }

    @Override
    public int hashCode() {
        return mTag.hashCode();
    }
}
