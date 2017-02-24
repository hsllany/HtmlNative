package com.mozz.remoteview.parser;

public class RVSyntaxError extends Exception {

    private long mLine;
    private long mColumn;

    RVSyntaxError(String msg, long line, long column) {
        super(msg);
        mLine = line;
        mColumn = column;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at [" + mLine + "," + mColumn + "]";
    }
}
