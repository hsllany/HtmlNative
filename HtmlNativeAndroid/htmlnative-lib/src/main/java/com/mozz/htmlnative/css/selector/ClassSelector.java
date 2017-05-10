package com.mozz.htmlnative.css.selector;

import com.mozz.htmlnative.dom.DomElement;

/**
 * @author Yang Tao, 17/3/27.
 */

public class ClassSelector extends TypeSelector {
    public ClassSelector(String classN) {
        super(classN);
    }

    @Override
    public String selfToString() {
        return "." + mTag;
    }

    @Override
    public boolean matchThis(DomElement element) {
        if (element.hasClazz()) {
            String[] classes = element.getClazz();
            for (String c : classes) {
                if (c.equals(mTag)) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }
}
