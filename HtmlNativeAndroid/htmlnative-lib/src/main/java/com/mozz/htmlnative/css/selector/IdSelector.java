package com.mozz.htmlnative.css.selector;

import com.mozz.htmlnative.dom.DomElement;

/**
 * @author Yang Tao, 17/3/27.
 */

public class IdSelector extends TypeSelector {
    public IdSelector(String Id) {
        super(Id);
    }

    @Override
    public String selfToString() {
        return "#" + mTag;
    }

    @Override
    public boolean matchThis(DomElement element) {
        return element.hasId() && element.getId().equals(mTag);
    }
}
