package com.mozz.remoteview.json.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class AttrsSet {
    private Map<String, Object> mAttrs;

    public AttrsSet() {
        mAttrs = new HashMap<>();
    }

    public void put(String paramsKey, String value) {
        mAttrs.put(paramsKey, value);
    }

    public void put(String paramsKey, double value) {
        mAttrs.put(paramsKey, value);
    }

    public void put(String paramsKey, int value) {
        mAttrs.put(paramsKey, value);
    }

    @Override
    public String toString() {
        return mAttrs.toString();
    }
}
