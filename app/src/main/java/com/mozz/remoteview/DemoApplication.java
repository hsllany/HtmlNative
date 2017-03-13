package com.mozz.remoteview;

import android.app.Application;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.leakcanary.LeakCanary;

import java.util.Arrays;

/**
 * @author Yang Tao, 17/3/1.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RV.getInstance().init(this);


        RV.getInstance().setImageViewAdapter(new ImageViewAdapter() {
            @Override
            public void setImage(String src, ImageView imageView) {
                long time1 = SystemClock.currentThreadTimeMillis();
                Glide.with(DemoApplication.this).load(src).into(imageView);

                Log.d("PerformanceWatcher", "---->Glide spend" + (SystemClock.currentThreadTimeMillis() - time1));
            }
        });

        RV.getInstance().setHrefLinkHandler(new HrefLinkHandler() {
            @Override
            public void onHref(String url, View view) {
                Toast.makeText(DemoApplication.this, url, Toast.LENGTH_SHORT).show();
            }
        });

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RV.getInstance().onDestroy();
    }
}
