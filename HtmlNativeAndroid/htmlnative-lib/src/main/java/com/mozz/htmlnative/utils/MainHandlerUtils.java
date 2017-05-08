package com.mozz.htmlnative.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

public final class MainHandlerUtils {

    @NonNull
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private MainHandlerUtils() {

    }

    public void post(Runnable r) {
        mHandler.post(r);
    }

    public void post(Runnable r, long delay) {
        mHandler.postDelayed(r, delay);
    }

    @NonNull
    private static MainHandlerUtils sInstance = new MainHandlerUtils();

    @NonNull
    public static MainHandlerUtils instance() {
        return sInstance;
    }
}
