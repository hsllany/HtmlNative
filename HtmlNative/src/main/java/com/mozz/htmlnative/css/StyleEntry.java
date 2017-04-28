package com.mozz.htmlnative.css;

import java.util.Map;

/**
 * @author Yang Tao, 17/4/28.
 */
public class StyleEntry implements Map.Entry<String, Object> {

    private String mParam;
    private Object mValue;

    public StyleEntry(String param, Object value) {
        this.mParam = param;
        this.mValue = value;
    }

    @Override
    public String getKey() {
        return mParam;
    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public Object setValue(Object value) {
        Object oldVal = mValue;
        mValue = value;
        return oldVal;
    }

    @Override
    public String toString() {
        return mParam + "=" + mValue;
    }
}
