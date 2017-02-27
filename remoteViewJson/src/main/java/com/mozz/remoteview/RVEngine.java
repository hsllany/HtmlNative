package com.mozz.remoteview;

import android.app.Activity;
import android.app.Fragment;
import android.view.ViewGroup;

import com.mozz.remoteview.parser.OnRViewLoaded;
import com.mozz.remoteview.parser.RVModule;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class RVEngine {

    private ThreadPoolExecutor mExecutor;

    private RVEngine() {
    }

    private static RVEngine sInstance = null;

    public static RVEngine getInstance() {
        if (sInstance == null) {
            synchronized (RVEngine.class) {
                if (sInstance == null) {
                    sInstance = new RVEngine();
                }
            }
        }

        return sInstance;
    }

    public static void setThreadPool(ThreadPoolExecutor executor) {
        getInstance().setExecutor(executor);
    }

    public static void loadView(InputStream inputStream, OnRViewLoaded onRViewLoaded) {

    }

    public static void loadView(InputStream inputStream, Activity activity) {

    }

    public static void loadView(InputStream inputStream, Fragment view) {

    }

    public static void loadView(InputStream inputStream, ViewGroup viewGroup) {

    }

    private void setExecutor(ThreadPoolExecutor mExecutor) {
        this.mExecutor = mExecutor;
    }

    public void onDestory() {
        RVModule.clearCache();
    }
}
