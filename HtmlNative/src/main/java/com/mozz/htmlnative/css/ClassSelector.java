package com.mozz.htmlnative.css;

import com.mozz.htmlnative.DomElement;

/**
 * @author Yang Tao, 17/3/27.
 */

public class ClassSelector extends TypeSelector {
    public ClassSelector(String classN) {
        super(classN);
    }

    @Override
    public String selfToString() {
        return "." + mTag;
    }

    @Override
    public boolean matchThis(DomElement element) {
        return element.hasClazz() && element.getClazz().equals(mTag);
    }
}
