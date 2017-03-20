package com.mozz.htmlnative;

/**
 * @author Yang Tao, 17/3/15.
 */

final class Meta {
    static final String ID_NAME = "name";
    static final String ID_CONTENT = "content";

    String name;
    String content;

    @Override
    public String toString() {
        return "Meta:content=" + content;
    }
}
