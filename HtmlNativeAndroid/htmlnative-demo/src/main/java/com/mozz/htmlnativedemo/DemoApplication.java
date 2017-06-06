package com.mozz.htmlnativedemo;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mozz.htmlnative.HNConfig;
import com.mozz.htmlnative.HNativeEngine;
import com.mozz.htmlnative.ImageFetcher;
import com.mozz.htmlnative.ScriptCallback;
import com.mozz.htmlnative.onHrefClick;
import com.mozz.htmlnative.view.BackgroundViewDelegate;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author Yang Tao, 17/3/1.
 */

public class DemoApplication extends Application {

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static DemoApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        HNConfig config = new HNConfig.Builder().setHttpClient(new DemoHttpClient())
                .setImageFetcher(new ImageFetcher() {
            @Override
            public void setImage(String src, final BackgroundViewDelegate imageView) {
                Glide.with(DemoApplication.this).load(src).asBitmap().into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        Log.d("GlideTest", "onResourceReady");
                        imageView.setBitmap(resource);
                    }
                });

            }
        }).setOnHrefClick(new onHrefClick() {
            @Override
            public void onHref(String url, View view) {
                Toast.makeText(DemoApplication.this, url, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }).setScriptCallback(new ScriptCallback() {
            @Override
            public void error(final Throwable e) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoApplication.this, e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        }).build();

        HNativeEngine.init(this, config);
        HNativeEngine.getInstance().debugRenderProcess();

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
        HNativeEngine.getInstance().destroy();
    }
}
