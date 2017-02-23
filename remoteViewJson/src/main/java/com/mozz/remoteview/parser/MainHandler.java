package com.mozz.remoteview.parser;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Yang Tao on 17/2/23.
 */

public class MainHandler {

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
