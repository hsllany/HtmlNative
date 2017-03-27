package com.mozz.htmlnative;

import android.support.annotation.NonNull;

import com.mozz.htmlnative.css.CssSelector;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang Tao, 17/3/27.
 */

public class Css {
    private AttrsSet mCssSet;
    private Set<CssSelector> mSelector;

    public Css() {
        mCssSet = new AttrsSet();
        mSelector = new HashSet<>();
    }

    public void newAttr(@NonNull AttrsOwner tree) {
        mCssSet.newAttr(tree);
    }

    public void putAttr(@NonNull AttrsOwner tree, String paramsKey, @NonNull Object value) {
        mCssSet.put(tree, paramsKey, value);
    }

    public void putSelector(CssSelector cssSelector) {
        mSelector.add(cssSelector);
    }

    @Override
    public String toString() {
        return "AttrSet=" + mCssSet.toString() + ", Selector=" + mSelector;
    }
}
