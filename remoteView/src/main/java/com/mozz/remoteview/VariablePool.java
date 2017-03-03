package com.mozz.remoteview;

import android.util.ArrayMap;

import java.util.Map;

/**
 * @author Yang Tao, 17/2/27.
 */

final class VariablePool {
    private Map<String, Object> mPool;

    VariablePool() {
        mPool = new ArrayMap<>();
    }

    void addVariable(String string, Object object) {
        mPool.put(string, object);
    }

    void updateVariable(String string, Object newValue) {
        mPool.put(string, newValue);
    }

    Object getVariable(String string) {
        return mPool.get(string);
    }
}
