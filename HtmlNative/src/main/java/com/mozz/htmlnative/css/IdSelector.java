package com.mozz.htmlnative.css;

/**
 * @author Yang Tao, 17/3/27.
 */

public class IdSelector extends TagSelector {
    public IdSelector(String Id) {
        super(Id);
    }

    @Override
    public String selfToString() {
        return "[Id=" + mTag + "]";
    }
}
