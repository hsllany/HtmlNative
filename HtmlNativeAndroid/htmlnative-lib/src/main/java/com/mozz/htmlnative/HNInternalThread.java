package com.mozz.htmlnative;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

/**
 * @author Yang Tao, 17/5/3.
 */

final class HNInternalThread {

    // for running render task
    @NonNull
    private static HandlerThread mRenderThread = new HandlerThread("HNInternalThread");
    private static Handler mRenderHandler;

    static void init() {
        mRenderThread.start();
        mRenderHandler = new Handler(mRenderThread.getLooper());
    }

    public static void run(Runnable r) {
        mRenderHandler.post(r);
    }

    static void quit() {
        mRenderThread.quit();
    }
}
