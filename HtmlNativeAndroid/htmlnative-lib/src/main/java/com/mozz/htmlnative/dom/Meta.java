package com.mozz.htmlnative.dom;

/**
 * @author Yang Tao, 17/3/15.
 */

public final class Meta {
    public static final String ID_NAME = "name";
    public static final String ID_CONTENT = "content";

    private String name;
    private String content;

    @Override
    public String toString() {
        return "Meta:content=" + content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
