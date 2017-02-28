package com.mozz.remoteview;

import android.os.Handler;
import android.os.Looper;

public final class MainHandler {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private MainHandler() {

    }

    public void post(Runnable r) {
        mHandler.post(r);
    }

    public void post(Runnable r, long delay) {
        mHandler.postDelayed(r, delay);
    }

    private static MainHandler sInstance = new MainHandler();

    public static MainHandler instance() {
        return sInstance;
    }
}
