package com.mozz.remoteview;

import android.app.Application;

/**
 * @author Yang Tao, 17/3/1.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RV.getInstance().start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RV.getInstance().onDestory();
    }
}
