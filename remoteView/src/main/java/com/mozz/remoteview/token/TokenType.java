package com.mozz.remoteview.token;

public enum TokenType {

    Unknown("Unknown"),

    LeftAngleBracket("<"),

    RightAngleBracket(">"),

    Slash("/"),

    Id("Id"),

    Int("Int"),

    Double("Double"),

    Equal("="),

    // for value in quot
    Value("Value"),

    Code("Script"),

    // Handle the inner text of an element
    Inner("Inner"),

    Template("Template"),

    Body("Body"),

    Script("Script");

    private String value;

    TokenType(String string) {
        this.value = string;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
