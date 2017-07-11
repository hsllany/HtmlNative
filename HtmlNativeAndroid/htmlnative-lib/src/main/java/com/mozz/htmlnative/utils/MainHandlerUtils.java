package com.mozz.htmlnative.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

public final class MainHandlerUtils {

    @NonNull
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private MainHandlerUtils() {

    }

    public void postAsynchronous(Runnable r) {

        Message renderMsg = Message.obtain(mHandler, r);
        renderMsg.setAsynchronous(true);

        mHandler.sendMessage(renderMsg);
    }

    public void postAsynchronousDelay(Runnable r, long delay) {
        mHandler.postDelayed(r, delay);
    }

    public void post(Runnable r) {
        mHandler.post(r);
    }

    @NonNull
    private static MainHandlerUtils sInstance = new MainHandlerUtils();

    @NonNull
    public static MainHandlerUtils instance() {
        return sInstance;
    }
}
