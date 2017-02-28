package com.mozz.remoteview.token;

public enum Type {

    Unknown("unknown"),

    LeftAngleBracket("<"),

    RightAngleBracket(">"),

    Slash("/"),

    Id("Id"),

    Int("Int"),

    Double("Double"),

    Equal("="),

    // for value in quot
    Value("Value"),

    Code("Code"),

    Template("template"),

    Script("script");

    private String value;

    Type(String string) {
        this.value = string;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
