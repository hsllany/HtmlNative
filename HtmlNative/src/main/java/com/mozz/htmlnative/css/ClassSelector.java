package com.mozz.htmlnative.css;

/**
 * @author Yang Tao, 17/3/27.
 */

public class ClassSelector extends TagSelector {
    public ClassSelector(String classN) {
        super(classN);
    }

    @Override
    public String selfToString() {
        return "[Class=" + mTag + "]";
    }
}
