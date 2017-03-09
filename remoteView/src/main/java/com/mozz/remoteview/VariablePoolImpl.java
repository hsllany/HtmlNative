package com.mozz.remoteview;

import android.util.ArrayMap;

import java.util.Map;

/**
 * @author Yang Tao, 17/2/27.
 */

final class VariablePoolImpl implements VariablePool {
    private Map<String, Object> mPool;

    VariablePoolImpl() {
        mPool = new ArrayMap<>();
    }

    @Override
    public void addVariable(String string, Object object) {
        mPool.put(string, object);
    }

    @Override
    public void updateVariable(String string, Object newValue) {
        mPool.put(string, newValue);
    }

    @Override
    public Object getVariable(String string) {
        return mPool.get(string);
    }
}
