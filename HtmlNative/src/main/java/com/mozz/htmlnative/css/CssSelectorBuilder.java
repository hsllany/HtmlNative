package com.mozz.htmlnative.css;

/**
 * @author Yang Tao, 17/3/27.
 */

public class CssSelectorBuilder {

    private CssSelector selector;

    public CssSelectorBuilder tag(String tagName) {
        if (selector == null) {
            selector = new TagSelector(tagName);
        } else {
            selector.chain(new TagSelector(tagName));
        }
        return this;
    }

    public CssSelectorBuilder id(String id) {
        if (selector == null) {
            selector = new IdSelector(id);
        } else {
            selector.chain(new IdSelector(id));
        }
        return this;
    }

    public CssSelectorBuilder clazz(String className) {
        if (selector == null) {
            selector = new ClassSelector(className);
        } else {
            selector.chain(new ClassSelector(className));
        }
        return this;
    }

    public CssSelector build() {
        return selector;
    }
}
