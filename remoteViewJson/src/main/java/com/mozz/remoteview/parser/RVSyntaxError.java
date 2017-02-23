package com.mozz.remoteview.parser;

public class RVSyntaxError extends Exception {

    private int mLine;

    RVSyntaxError(String msg, int line) {
        super(msg);
        mLine = line;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at line " + mLine;
    }
}
