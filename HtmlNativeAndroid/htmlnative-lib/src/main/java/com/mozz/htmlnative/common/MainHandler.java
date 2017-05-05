package com.mozz.htmlnative.common;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

public final class MainHandler {

    @NonNull
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private MainHandler() {

    }

    public void post(Runnable r) {
        mHandler.post(r);
    }

    public void post(Runnable r, long delay) {
        mHandler.postDelayed(r, delay);
    }

    @NonNull
    private static MainHandler sInstance = new MainHandler();

    @NonNull
    public static MainHandler instance() {
        return sInstance;
    }
}
