package com.mozz.htmlnative.parser.syntaxexc;

import com.mozz.htmlnative.exception.HNSyntaxError;

import java.io.EOFException;

/**
 * @author Yang Tao, 17/6/14.
 */

public interface SyntaxExceptionSource {

    long getLine();

    long getColumn();

    void onSyntaxException() throws HNSyntaxError, EOFException;
}