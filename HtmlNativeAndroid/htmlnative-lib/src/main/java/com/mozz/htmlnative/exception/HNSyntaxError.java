package com.mozz.htmlnative.exception;

import android.support.annotation.NonNull;

public class HNSyntaxError extends Exception {

    public HNSyntaxError(String msg) {
        super(msg);
    }

    @NonNull
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
