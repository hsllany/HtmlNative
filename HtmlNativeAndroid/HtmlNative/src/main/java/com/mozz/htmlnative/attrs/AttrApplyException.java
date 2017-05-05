package com.mozz.htmlnative.attrs;

/**
 * @author Yang Tao, 17/3/3.
 */
public class AttrApplyException extends Exception {

    public AttrApplyException() {
        super();
    }

    public AttrApplyException(Throwable cause){
        super(cause);
    }

    public AttrApplyException(String msg, Throwable cause){
        super(msg, cause);
    }

    public AttrApplyException(String msg) {
        super(msg);
    }
}
