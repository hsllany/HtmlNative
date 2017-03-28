package com.mozz.htmlnative.css;

/**
 * @author Yang Tao, 17/3/27.
 */

public class ClassSelector extends TypeSelector {
    public ClassSelector(String classN) {
        super(classN);
    }

    @Override
    public String selfToString() {
        return "[Class=" + mTag + "]";
    }

    @Override
    public boolean matchThis(String type, String id, String clazz) {
        return mTag.equals(clazz);
    }
}
