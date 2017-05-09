package com.mozz.htmlnative.css;

import com.mozz.htmlnative.css.selector.AnySelector;
import com.mozz.htmlnative.css.selector.ClassSelector;
import com.mozz.htmlnative.css.selector.CssSelector;
import com.mozz.htmlnative.css.selector.IdSelector;
import com.mozz.htmlnative.css.selector.TypeSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yang Tao, 17/3/27.
 */

public final class StyleSheet extends AttrsSet {

    private StringSelectorHolder mClassSelectors;
    private StringSelectorHolder mIdSelectors;
    private StringSelectorHolder mTypeSelectors;
    private AnySelectorHolder mAnySelectors;

    public StyleSheet() {
        super("StyleSheet");

        mClassSelectors = new StringSelectorHolder();
        mIdSelectors = new StringSelectorHolder();
        mTypeSelectors = new StringSelectorHolder();
        mAnySelectors = new AnySelectorHolder();
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
        } else if (cssSelector.getClass().equals(AnySelector.class)) {
            mAnySelectors.put((AnySelector) cssSelector);
        }
    }

    public Set<CssSelector> matchedSelector(String type, String id, String clazz) {

        // FIXME: 17/5/9 次序问题
        Set<CssSelector> matchedSelector = new HashSet<>();
        mClassSelectors.matches(clazz, matchedSelector);
        mIdSelectors.matches(id, matchedSelector);
        mTypeSelectors.matches(type, matchedSelector);
        mAnySelectors.matches(matchedSelector);
        return matchedSelector;
    }

    @Override
    public String toString() {
        return "AttrSet=" + super.toString() + "\n, class=" + mClassSelectors + "\n, id=" +
                mIdSelectors + "\n, type=" + mTypeSelectors;
    }

    /**
     * @author Yang Tao, 17/3/30.
     */

    private static final class StringSelectorHolder {
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

    private static final class AnySelectorHolder {
        private List<CssSelector> mSelectors = new ArrayList<>();

        public void put(AnySelector selector) {
            mSelectors.add(selector);
        }

        void matches(Set<CssSelector> outSelectors) {
            outSelectors.addAll(mSelectors);
        }

        @Override
        public String toString() {
            return mSelectors.toString();
        }
    }
}
