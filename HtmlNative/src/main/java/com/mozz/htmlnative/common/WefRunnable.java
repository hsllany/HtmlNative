package com.mozz.htmlnative.common;

import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/3/1.
 */
public abstract class WefRunnable<T> implements Runnable {

    WeakReference<T> mRef;

    public WefRunnable(T context) {
        mRef = new WeakReference<>(context);
    }

    @Override
    public final void run() {
        T raw = mRef.get();

        runOverride(raw);
    }

    protected abstract void runOverride(T t);
}
