package com.mozz.htmlnative;

import android.support.annotation.NonNull;

import com.mozz.htmlnative.attrs.AttrsOwner;
import com.mozz.htmlnative.css.ClassSelector;
import com.mozz.htmlnative.css.CssSelector;
import com.mozz.htmlnative.css.IdSelector;
import com.mozz.htmlnative.css.TypeSelector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Yang Tao, 17/3/27.
 */

final class StyleSheet {
    AttrsSet mCssSet;

    private SelectorHolder mClassSelectors;
    private SelectorHolder mIdSelectors;
    private SelectorHolder mTypeSelectors;

    public StyleSheet() {
        mCssSet = new AttrsSet("StyleSheet");

        mClassSelectors = new SelectorHolder();
        mIdSelectors = new SelectorHolder();
        mTypeSelectors = new SelectorHolder();
    }

    public void newAttr(@NonNull AttrsOwner tree) {
        mCssSet.newAttr(tree);
    }

    public void putAttr(@NonNull AttrsOwner tree, String paramsKey, @NonNull Object value) {
        mCssSet.put(tree, paramsKey, value);
    }

    public void putSelector(CssSelector cssSelector) {
        putSingleSelector(cssSelector.tail());
    }

    private void putSingleSelector(CssSelector cssSelector) {
        if (cssSelector.getClass().equals(ClassSelector.class)) {
            ClassSelector classSelector = (ClassSelector) cssSelector;
            mClassSelectors.put(classSelector.getName(), classSelector);
        } else if (cssSelector.getClass().equals(IdSelector.class)) {
            IdSelector idSelector = (IdSelector) cssSelector;
            mIdSelectors.put(idSelector.getName(), idSelector);
        } else if (cssSelector.getClass().equals(TypeSelector.class)) {
            TypeSelector typeSelector = (TypeSelector) cssSelector;
            mTypeSelectors.put(typeSelector.getName(), typeSelector);
        }
    }

    public Set<CssSelector> matchedSelector(String type, String id, String clazz) {
        Set<CssSelector> matchedSelector = new HashSet<>();
        mClassSelectors.matches(clazz, matchedSelector);
        mIdSelectors.matches(id, matchedSelector);
        mTypeSelectors.matches(type, matchedSelector);
        return matchedSelector;
    }

    @Override
    public String toString() {
        return "AttrSet=" + mCssSet.toString() + "\n, class=" + mClassSelectors + "\n, id=" +
                mIdSelectors + "\n, type=" + mTypeSelectors;
    }

    /**
     * @author Yang Tao, 17/3/30.
     */

    private static final class SelectorHolder {
        private Map<String, Set<CssSelector>> mSelectors = new HashMap<>();

        public void put(String key, CssSelector selector) {
            Set<CssSelector> sets = mSelectors.get(key);
            if (sets == null) {
                sets = new HashSet<>();
                mSelectors.put(key, sets);
            }

            sets.add(selector);
        }

        void matches(String key, Set<CssSelector> outSelectors) {
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
}
