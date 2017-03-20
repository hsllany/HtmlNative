package com.mozz.htmlnative;

import android.util.ArrayMap;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/15.
 */

final class MetaData {
    private Map<String, Meta> metaMap;

    public MetaData() {
        metaMap = new ArrayMap<>(4);
    }

    public boolean contains(Meta key) {
        return metaMap.containsKey(key.name);
    }

    public Meta get(String metaName) {
        return metaMap.get(metaName);
    }

    public Meta put(Meta value) {
        return metaMap.put(value.name, value);
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
