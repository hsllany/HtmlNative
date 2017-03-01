package com.mozz.remoteview;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.remoteview.code.LuaRunner;
import com.mozz.remoteview.common.MainHandler;
import com.mozz.remoteview.common.WefRunnable;

import java.io.InputStream;

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

    public void stop() {

    }

    public void loadView(final Context context, final InputStream inputStream, final OnRViewLoaded onRViewLoaded) {
        RVRenderer.runRenderTask(new WefRunnable<Context>(context) {
            @Override
            public void runOverride(Context innerContext) {
                try {
                    if (innerContext == null)
                        return;

                    RVModule module = RVModule.load(inputStream);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    final View v = RVRenderer.get().inflate(innerContext, module, null, false, layoutParams);

                    MainHandler.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (onRViewLoaded != null)
                                onRViewLoaded.onViewLoaded(v);
                        }
                    });
                } catch (RVSyntaxError rvSyntaxError) {
                    rvSyntaxError.printStackTrace();
                } catch (RVRenderer.RemoteInflateException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void loadView(Context context, InputStream inputStream, Activity activity) {

    }

    public void loadView(Context context, InputStream inputStream, Fragment view) {

    }

    public void loadView(Context context, InputStream inputStream, ViewGroup viewGroup) {

    }

    public static String version() {
        return Version.v;
    }

    public static int versionCode() {
        return Version.versionCode;
    }

    public void onDestory() {
        RVModule.clearCache();
        RVRenderer.quit();
        LuaRunner.getInstance().quit();
    }

    public interface OnRViewLoaded {
        void onViewLoaded(View v);
    }
}
