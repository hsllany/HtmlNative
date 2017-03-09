package com.mozz.remoteview;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mozz.remoteview.script.LuaRunner;
import com.mozz.remoteview.common.MainHandler;
import com.mozz.remoteview.common.WefRunnable;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/21.
 */

public final class RV {

    public static final String LUA_TAG = "RVScript";

    private static final String TAG = "RV";

    private DisplayMetrics mDefaultMetrics;

    private RV() {
        RVRenderer.init();
    }

    private static RV sInstance = null;

    public static RV getInstance() {
        if (sInstance == null) {
            synchronized (RV.class) {
                if (sInstance == null) {
                    sInstance = new RV();
                }
            }
        }

        return sInstance;
    }

    public void init(Context context) {
        initScreenMetrics(context);
    }

    DisplayMetrics getScreenMetrics() {
        return mDefaultMetrics;
    }

    private void initScreenMetrics(Context context) {
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        mDefaultMetrics = new DisplayMetrics();
        display.getMetrics(mDefaultMetrics);
    }

    public final void loadView(final Context context, final InputStream inputStream, final OnRViewLoaded onRViewLoaded) {
        RVRenderer.runRenderTask(new WefRunnable<Context>(context) {
            @Override
            public void runOverride(final Context innerContext) {
                try {
                    if (innerContext == null)
                        return;

                    final RVModule module = RVModule.load(inputStream);

                    Log.d(TAG, module.mRootTree.wholeTreeToString());

                    final ViewGroup.LayoutParams layoutParams =
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                    MainHandler.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            View v = null;
                            try {
                                v = RVRenderer.get().inflate(innerContext, module, layoutParams);
                            } catch (RVRenderer.RemoteInflateException e) {
                                e.printStackTrace();
                            }

                            if (onRViewLoaded != null)
                                onRViewLoaded.onViewLoaded(v);
                        }
                    });
                } catch (RVSyntaxError e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void loadView(Context context, InputStream inputStream, Activity activity) {
        loadView(context, inputStream, new OnRViewLoadedWeak<Activity>(activity) {
            @Override
            public void onViewLoaded(View v) {
                Activity act = mWeakRef.get();
                if (act != null && !act.isDestroyed() && v != null) {
                    act.setContentView(v);
                }
            }
        });
    }

    public void loadView(Context context, InputStream inputStream, final ViewGroup viewGroup) {
        loadView(context, inputStream, new OnRViewLoadedWeak<ViewGroup>(viewGroup) {
            @Override
            public void onViewLoaded(View v) {
                ViewGroup vv = mWeakRef.get();
                vv.addView(v);
            }
        });
    }

    public static String version() {
        return RVEnvironment.v;
    }

    public static int versionCode() {
        return RVEnvironment.versionCode;
    }

    /**
     * @param tag
     * @param rView
     */
    public static void registerRView(String tag, RView rView) {
        ViewRegistry.registerExtraView(tag, rView);
    }

    public void onDestroy() {
        RVModule.clearCache();
        RVRenderer.quit();
        LuaRunner.getInstance().quit();
    }

    public interface OnRViewLoaded {
        void onViewLoaded(View v);
    }

    private abstract class OnRViewLoadedWeak<T> implements OnRViewLoaded {
        protected WeakReference<T> mWeakRef;

        public OnRViewLoadedWeak(T tt) {
            this.mWeakRef = new WeakReference<>(tt);
        }
    }
}
