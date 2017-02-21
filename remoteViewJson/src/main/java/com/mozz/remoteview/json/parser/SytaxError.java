package com.mozz.remoteview.json.parser;

public class SytaxError extends Exception {

    private int mLine;

    SytaxError(String msg, int line) {
        super(msg);
        mLine = line;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at line " + mLine;
    }
}
