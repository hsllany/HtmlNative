package com.mozz.remoteview.json.parser.token;

/**
 * Created by Yang Tao on 17/2/21.
 */

public enum Type {
    LeftAngleBracket("<"),
    RightAngleBracket(">"),
    Slash("/"),
    Id("Id"),
    Int("Int"),
    Double("Double"),
    Quot("\""),
    Equal("="),

    // for value in quot
    Value("Value"),
    // for inner text
    Element("Element");

    private String value;

    Type(String string) {
        this.value = string;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
