package com.mozz.htmlnative.css.selector;

import com.mozz.htmlnative.dom.DomElement;

/**
 * @author Yang Tao, 17/3/27.
 */

public class TypeSelector extends CssSelector {

    protected String mTag;

    public TypeSelector(String tag) {
        super();
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
