package com.mozz.remoteview;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.remoteview.common.MainHandler;
import com.mozz.remoteview.common.WefRunnable;

import java.io.InputStream;

/**
 * @author Yang Tao, 17/3/10.
 */

final class ProcessThread {

    private static final String TAG = ProcessThread.class.getSimpleName();

    private ProcessThread() {

    }

    // for running render task
    @NonNull
    private static HandlerThread mRenderThread = new HandlerThread("RVRenderThread");
    private static Handler mRenderHandler;

    static void init() {
        mRenderThread.start();
        mRenderHandler = new Handler(mRenderThread.getLooper());
    }

    static void quit() {
        mRenderThread.quit();
    }

    static void runRenderTask(@NonNull RenderTask r) {
        mRenderHandler.post(r);
    }

    static final class RenderTask extends WefRunnable<Context> {

        private InputStream mFileSource;
        private RV.OnRViewLoaded mCallback;

        RenderTask(Context context, InputStream fileSource, RV.OnRViewLoaded callback) {
            super(context);
            mFileSource = fileSource;
            mCallback = callback;
        }

        @Override
        protected void runOverride(@Nullable final Context context) {
            try {
                if (context == null) {
                    return;
                }

                final RVSegment module = RVSegment.load(mFileSource);

                Log.d(TAG, module.mRootTree.wholeTreeToString());

                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                MainHandler.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        View v = null;
                        try {
                            v = RVRenderer.get().render(context, module, layoutParams);
                        } catch (RVRenderer.RemoteInflateException e) {
                            e.printStackTrace();
                        }

                        if (mCallback != null) {
                            mCallback.onViewLoaded(v);
                        }
                    }
                });
            } catch (@NonNull final RVSyntaxError e) {
                e.printStackTrace();
                if (mCallback != null) {
                    MainHandler.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallback != null) {
                                mCallback.onError(e);
                            }
                        }
                    });
                }
            }
        }
    }
}
