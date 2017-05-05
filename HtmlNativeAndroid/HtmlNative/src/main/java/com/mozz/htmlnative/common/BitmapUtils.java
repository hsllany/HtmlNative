package com.mozz.htmlnative.common;

import android.graphics.Bitmap;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.mozz.htmlnative.HNInternalThread;

/**
 * @author Yang Tao, 17/5/3.
 */

public class BitmapUtils {
    private BitmapUtils() {
    }

    public static void process(final Bitmap bitmap, final ProcessTask task) {
        HNInternalThread.run(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap1 = task.process(bitmap);
                MainHandler.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        task.done(bitmap1);
                    }
                });
            }
        });
    }

    public interface ProcessTask {

        @WorkerThread
        Bitmap process(Bitmap raw);

        @MainThread
        void done(Bitmap bitmap);
    }
}
