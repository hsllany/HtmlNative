package com.mozz.htmlnative.parser.syntaxexc;

import com.mozz.htmlnative.exception.HNSyntaxError;

import java.io.EOFException;

/**
 * @author Yang Tao, 17/6/14.
 */
public class SyntaxErrorHandler {
    private ErrorStack mErrorStack;
    private SyntaxExceptionSource mSource;

    public SyntaxErrorHandler(ErrorStack stack, SyntaxExceptionSource source) {
        mErrorStack = stack;
        mSource = source;
    }

    private SyntaxErrorHandler(ErrorStack stack) {
        mErrorStack = stack;
    }

    public void setSource(SyntaxExceptionSource source) {
        mSource = source;
    }

    public void throwException(String msg) throws HNSyntaxError, EOFException {
        SyntaxException exception = new SyntaxException(msg, mSource);
        try {
            mErrorStack.newException(exception);
            mSource.onSyntaxException();
        } catch (HNSyntaxError e) {
            // catch this because onSyntaxException dose not have to run.
            throw e;
        }

    }

    public void throwException(String msg, final long line, final long column) throws
            HNSyntaxError, EOFException {
        SyntaxException exception = new SyntaxException(msg, new SyntaxExceptionSource() {
            @Override
            public long getLine() {
                return line;
            }

            @Override
            public long getColumn() {
                return column;
            }

            @Override
            public void onSyntaxException() throws HNSyntaxError, EOFException {

            }
        });

        try {
            mErrorStack.newException(exception);
            mSource.onSyntaxException();
        } catch (HNSyntaxError e) {
            // catch this because onSyntaxException dose not have to run.
            throw e;
        }
    }

    public SyntaxErrorHandler newChildHandler() {
        return new SyntaxErrorHandler(this.mErrorStack);
    }

    public String forceDump() {
        return mErrorStack.forceDump();
    }

    public boolean hasError() {
        return mErrorStack.hasError();
    }


}
