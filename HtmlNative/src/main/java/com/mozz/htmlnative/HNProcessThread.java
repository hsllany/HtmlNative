package com.mozz.htmlnative;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.common.MainHandler;
import com.mozz.htmlnative.common.WefRunnable;

import java.io.InputStream;

/**
 * @author Yang Tao, 17/3/10.
 */

final class HNProcessThread {

    private static final String TAG = HNProcessThread.class.getSimpleName();

    private HNProcessThread() {

    }

    // for running render task
    @NonNull
    private static HandlerThread mRenderThread = new HandlerThread("HNProcessThread");
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
        private final HNative.OnHNViewLoaded mCallback;

        RenderTask(Context context, InputStream fileSource, HNative.OnHNViewLoaded callback) {
            super(context);
            mFileSource = fileSource;
            mCallback = callback;
        }

        @Override
        protected void runOverride(@Nullable final Context context) {
            try {
                if (context == null || mCallback == null) {
                    return;
                }

                final HNSegment segment = HNSegment.load(mFileSource);

                Log.d(TAG, segment.mRootTree.wholeTreeToString());
                Log.d(TAG, segment.toString());
                if (segment.mScriptInfo != null) {
                    Log.d(TAG, segment.mScriptInfo.toString());
                }

                MainHandler.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onHead(segment.mHead);
                    }
                });

                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                MainHandler.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        View v = null;
                        try {
                            v = HNRenderer.get().render(context, segment, layoutParams);
                        } catch (HNRenderer.RemoteInflateException e) {
                            e.printStackTrace();
                        }

                        mCallback.onViewLoaded(v);
                    }
                });
            } catch (@NonNull final HNSyntaxError e) {
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
