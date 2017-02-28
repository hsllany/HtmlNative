package com.mozz.remoteview.parser;

import android.util.ArrayMap;

import java.util.Map;

/**
 * @author Yang Tao, 17/2/27.
 */

public class VariablePool {
    private Map<String, Object> mPool;

    public VariablePool() {
        mPool = new ArrayMap<>();
    }

    public void addVariable(String string, Object object) {
        mPool.put(string, object);
    }

    public void updateVariable(String string, Object newValue) {
        mPool.put(string, newValue);
    }

    public Object getVariable(String string) {
        return mPool.get(string);
    }
}
