package com.mozz.htmlnative;

import com.mozz.htmlnative.css.CssSelector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Yang Tao, 17/3/30.
 */

final class SelectorHolder {
    private Map<String, Set<CssSelector>> mSelectors = new HashMap<>();

    public void put(String key, CssSelector selector) {
        Set<CssSelector> sets = mSelectors.get(key);
        if (sets == null) {
            sets = new HashSet<>();
            mSelectors.put(key, sets);
        }

        sets.add(selector);
    }

    void select(String key, Set<CssSelector> outSelectors) {
        Set<CssSelector> sets = mSelectors.get(key);
        if (sets != null && !sets.isEmpty()) {
            outSelectors.addAll(sets);
        }
    }

    @Override
    public String toString() {
        return mSelectors.toString();
    }
}
