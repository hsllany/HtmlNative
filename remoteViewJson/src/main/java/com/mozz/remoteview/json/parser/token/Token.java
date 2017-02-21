package com.mozz.remoteview.json.parser.token;


public class Token {
    private Type mType;

    private Object mValue;

    public Token(Type type) {
        this(type, null);
    }

    public Token(Type type, Object value) {
        mType = type;
        mValue = value;
    }

    @Override
    public String toString() {
        String value = mValue == null ? "" : ":" + mValue;
        return "[" + mType.toString() + "]" + value;
    }

    public Type type() {
        return mType;
    }

    public Object value() {
        return mValue;
    }

    public String stringValue() {
        return (String) mValue;
    }

    public int intValue() {
        return (int) mValue;
    }

    public double doubleValue() {
        return (double) mValue;
    }
}
