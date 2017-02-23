package com.mozz.remoteview.parser;

public class SyntaxError extends Exception {

    private int mLine;

    SyntaxError(String msg, int line) {
        super(msg);
        mLine = line;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at line " + mLine;
    }
}
