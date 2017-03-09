package com.mozz.remoteview.common;

/**
 * @author Yang Tao, 17/3/1.
 */

public abstract class StrRunnable<T> implements Runnable {

    T mRef;

    public StrRunnable(T t) {
        mRef = t;
    }

    @Override
    public void run() {
        runOverride(mRef);
    }

    protected abstract void runOverride(T t);
}
