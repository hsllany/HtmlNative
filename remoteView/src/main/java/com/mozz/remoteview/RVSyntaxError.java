package com.mozz.remoteview;

import android.support.annotation.NonNull;

public class RVSyntaxError extends Exception {

    private long mLine;
    private long mColumn;

    RVSyntaxError(String msg, long line, long column) {
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
