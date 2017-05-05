package com.mozz.htmlnative.exception;

import android.support.annotation.NonNull;

public class HNSyntaxError extends Exception {

    private long mLine;
    private long mColumn;

    public HNSyntaxError(String msg, long line, long column) {
        super(msg);
        mLine = line;
        mColumn = column;
    }

    @NonNull
    @Override
    public String getMessage() {
        return super.getMessage() + " at [" + mLine + "," + mColumn + "]";
    }
}
