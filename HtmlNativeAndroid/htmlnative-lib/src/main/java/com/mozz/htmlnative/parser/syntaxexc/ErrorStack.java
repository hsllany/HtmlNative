package com.mozz.htmlnative.parser.syntaxexc;

import com.mozz.htmlnative.exception.HNSyntaxError;

/**
 * @author Yang Tao, 17/6/12.
 */

public final class ErrorStack {

    private SyntaxError mNormalHead;
    private SyntaxError mNormal;
    private int mNormalLength;

    private static class SyntaxError {
        SyntaxException exception;
        SyntaxError next;
    }

    void newException(SyntaxException exception) throws HNSyntaxError {
        SyntaxError error = new SyntaxError();
        error.exception = exception;
        addToNormalExceptionList(error);
    }

    private void addToNormalExceptionList(SyntaxError syntaxError) throws HNSyntaxError {
        if (mNormalHead == null) {
            mNormalHead = syntaxError;
            mNormal = mNormalHead;
        } else {
            mNormal.next = syntaxError;
            mNormal = mNormal.next;
        }

        mNormalLength++;
        int MAX_NORMAL_STACK_ERROR_LENGTH = 30;
        if (mNormalLength > MAX_NORMAL_STACK_ERROR_LENGTH) {
            StringBuilder detailMsg = new StringBuilder();
            this.dump(detailMsg);
            throw new HNSyntaxError("too much errors:" + detailMsg.toString());
        }
    }

    private void dump(StringBuilder string) {
        SyntaxError cur = mNormalHead;
        while (cur != null) {
            string.append(cur.exception.getMessage()).append('\n');
            cur = cur.next;
        }
    }

    public String forceDump() {
        StringBuilder sb = new StringBuilder();
        dump(sb);
        return sb.toString();
    }

    public boolean hasError() {
        return mNormalLength != 0;
    }

}
