package com.mozz.htmlnative.css;

/**
 * @author Yang Tao, 17/3/27.
 */

public class IdSelector extends TypeSelector {
    public IdSelector(String Id) {
        super(Id);
    }

    @Override
    public String selfToString() {
        return "[Id=" + mTag + "]";
    }

    @Override
    public boolean matchThis(String type, String id, String clazz) {
        return mTag.equals(id);
    }
}
