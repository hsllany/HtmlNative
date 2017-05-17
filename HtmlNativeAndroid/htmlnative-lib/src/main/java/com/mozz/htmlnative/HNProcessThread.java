package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mozz.htmlnative.common.WefRunnable;
import com.mozz.htmlnative.exception.HNSyntaxError;
import com.mozz.htmlnative.utils.MainHandlerUtils;

import java.io.InputStream;

/**
 * @author Yang Tao, 17/3/10.
 */

final class HNProcessThread {

    private static final String TAG = HNProcessThread.class.getSimpleName();

    private HNProcessThread() {

    }

    static void runRenderTask(@NonNull RenderTask r) {
        HNInternalThread.run(r);
    }

    static final class RenderTask extends WefRunnable<Context> {

        private InputStream mFileSource;
        private final HNativeEngine.OnHNViewLoaded mCallback;

        RenderTask(Context context, InputStream fileSource, HNativeEngine.OnHNViewLoaded callback) {
            super(context);
            mFileSource = fileSource;
            mCallback = callback;
        }

        @Override
        protected void run(@Nullable final Context context) {
            try {
                if (context == null || mCallback == null) {
                    return;
                }

                final HNSegment segment = HNSegment.load(mFileSource);

                HNLog.d(HNLog.PROCESS_THREAD, "DOM: \n" + segment.getDom().wholeTreeToString());
                HNLog.d(HNLog.PROCESS_THREAD, "HEAD: \n" + segment.getHead().toString());
                HNLog.d(HNLog.PROCESS_THREAD, "CSS \n" + segment.getStyleSheet().toString());
                if (segment.getScriptInfo() != null) {
                    HNLog.d(HNLog.PROCESS_THREAD, "SCRIPT " + segment.getScriptInfo().toString());
                }

                MainHandlerUtils.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onHead(segment.getHead());
                    }
                });

                final ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                MainHandlerUtils.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        View v = null;
                        try {
                            v = HNRenderer.get().render(context, segment, layoutParams);
                        } catch (HNRenderer.HNRenderException e) {
                            e.printStackTrace();
                        }

                        mCallback.onViewLoaded(v);
                    }
                });
            } catch (@NonNull final HNSyntaxError e) {
                e.printStackTrace();
                if (mCallback != null) {
                    MainHandlerUtils.instance().post(new Runnable() {
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
