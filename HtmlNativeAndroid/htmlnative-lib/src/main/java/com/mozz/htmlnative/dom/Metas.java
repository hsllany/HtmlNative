package com.mozz.htmlnative.dom;

import android.util.ArrayMap;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/15.
 */

final class Metas {
    private Map<String, Meta> metaMap;

    public Metas() {
        metaMap = new ArrayMap<>(4);
    }

    public boolean contains(Meta key) {
        return metaMap.containsKey(key.getName());
    }

    public Meta get(String metaName) {
        return metaMap.get(metaName);
    }

    public Meta put(Meta value) {
        return metaMap.put(value.getName(), value);
    }

    public Meta remove(Meta key) {
        return metaMap.remove(key);
    }

    public void clear() {
        metaMap.clear();
    }

    @Override
    public String toString() {
        return metaMap.toString();
    }
}
