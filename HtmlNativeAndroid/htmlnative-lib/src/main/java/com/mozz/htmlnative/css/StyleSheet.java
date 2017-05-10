package com.mozz.htmlnative.css;

import android.util.Log;

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

    /**
     * used to store the css selector order in file
     */
    private Map<CssSelector, Integer> mSelectorOrderMap = new HashMap<>();

    private int mInsertOrderSave = 0;

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

        mSelectorOrderMap.put(cssSelector, mInsertOrderSave++);

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

    /**
     * Find selectors according to type, id and class. All selectors found will be stored in
     * insert order.
     *
     * @param type  type of element
     * @param id    id of element if have
     * @param clazz class name of element if have
     * @return List containing all the selectors matched in insert order.
     */
    public CssSelector[] matchedSelector(String type, String id, String[] clazz) {

        // Pass mSelectorOrderMap.size() to make sure that matchedSelector is big enough to hold
        // all the selectors found.
        CssSelector[] matchedSelector = new CssSelector[mSelectorOrderMap.size()];

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

    private final class StringSelectorHolder {
        private Map<String, Set<CssSelector>> mSelectors = new HashMap<>();

        public void put(String key, CssSelector selector) {
            Set<CssSelector> sets = mSelectors.get(key);
            if (sets == null) {
                sets = new HashSet<>();
                mSelectors.put(key, sets);
            }

            sets.add(selector);
        }

        void matches(String key, CssSelector[] outSelectors) {
            Set<CssSelector> sets = mSelectors.get(key);

            if (sets != null) {
                for (CssSelector selector : sets) {
                    Integer index = mSelectorOrderMap.get(selector);

                    Log.d("InsertBug", index + ", " + outSelectors.length + ", map=" +
                            mSelectorOrderMap.size());
                    outSelectors[index] = selector;
                }
            }
        }

        void matches(String[] key, CssSelector[] outSelectors) {
            if (key != null && key.length > 0) {
                for (String k : key) {
                    if (k != null) {
                        matches(k, outSelectors);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return mSelectors.toString();
        }
    }

    private final class AnySelectorHolder {
        private List<CssSelector> mSelectors = new ArrayList<>();

        public void put(AnySelector selector) {
            mSelectors.add(selector);
        }

        void matches(CssSelector[] outSelectors) {
            for (CssSelector selector : mSelectors) {
                Integer index = mSelectorOrderMap.get(selector);
                outSelectors[index] = selector;
            }
        }

        @Override
        public String toString() {
            return mSelectors.toString();
        }
    }
}
