package com.mozz.htmlnative.css.selector;

import com.mozz.htmlnative.dom.DomElement;

/**
 * @author Yang Tao, 17/5/9.
 */

public class AnySelector extends CssSelector {
    @Override
    public boolean matchThis(DomElement element) {
        return true;
    }

    @Override
    public String selfToString() {
        return "*";
    }
}
