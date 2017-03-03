package com.mozz.remoteview;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.remoteview.script.LuaRunner;
import com.mozz.remoteview.common.MainHandler;
import com.mozz.remoteview.common.WefRunnable;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/21.
 */

public class RV {

    public static final String LUA_TAG = "RVScript";


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

    }

    public void loadView(final Context context, final InputStream inputStream, final OnRViewLoaded onRViewLoaded) {
        RVRenderer.runRenderTask(new WefRunnable<Context>(context) {
            @Override
            public void runOverride(Context innerContext) {
                try {
                    if (innerContext == null)
                        return;

                    RVModule module = RVModule.load(inputStream);

                    ViewGroup.LayoutParams layoutParams =
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);

                    final View v = RVRenderer.get().inflate(innerContext, module, layoutParams);

                    MainHandler.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (onRViewLoaded != null)
                                onRViewLoaded.onViewLoaded(v);
                        }
                    });
                } catch (RVSyntaxError e) {
                    e.printStackTrace();
                } catch (RVRenderer.RemoteInflateException e) {
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

    public void onDestory() {
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
