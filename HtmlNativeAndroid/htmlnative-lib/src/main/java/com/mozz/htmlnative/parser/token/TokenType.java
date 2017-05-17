package com.mozz.htmlnative.parser.token;

public enum TokenType {

    Unknown("Unknown"),

    StartAngleBracket("<"),

    EndAngleBracket(">"),

    Slash("/"),

    Id("Id"),

    Int("Int"),

    Double("Double"),

    Equal("="),

    // for value in quot
    Value("Value"),

    // Handle the inner text of an element
    Inner("Inner"),

    Template("Template"),

    Body("Body"),

    Script("Script"),

    Head("Head"),

    Meta("Meta"),

    Link("Link"),

    Title("Title"),

    Html("Html"),

    Hash("#"),

    Dot("."),

    Colon(":"),

    StartParen("("),

    EndParen(")"),

    Semicolon(";"),

    StartBrace("{"),

    Style("Style"),

    Comma(","),

    EndBrace("}"),

    ScriptCode("ScriptCode"),

    Star("*"),

    Exclamation("!");

    private String value;

    TokenType(String string) {
        this.value = string;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
