package com.mozz.htmlnative.parser.syntaxexc;

/**
 * @author Yang Tao, 17/6/14.
 */

public class SyntaxException extends Exception {

    public SyntaxException(String msg, SyntaxExceptionSource provider) {
        super("[line=" + provider.getLine() + ", column=" + provider.getColumn() + "] " + msg);
    }
}
