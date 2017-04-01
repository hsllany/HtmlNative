package com.mozz.htmlnativedemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNative;
import com.mozz.htmlnative.HrefLinkHandler;
import com.mozz.htmlnative.ImageViewAdapter;
import com.mozz.htmlnative.WebViewCreator;
import com.mozz.htmlnative.view.ViewImageAdapter;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author Yang Tao, 17/3/1.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HNative.getInstance().init(this);
        HNative.getInstance().debugAll();

        HNLog.setDebugLevel(HNLog.PARSER);

        HNative.getInstance().setImageViewAdapter(new ImageViewAdapter() {
            @Override
            public void setImage(String src, final ViewImageAdapter imageView) {
                Glide.with(DemoApplication.this).load(src).asBitmap().into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        Log.d("GlideTest", "onResourceReady");
                        imageView.setImage(resource);
                    }
                });

            }
        });

        HNative.getInstance().setHrefLinkHandler(new HrefLinkHandler() {
            @Override
            public void onHref(String url, View view) {
                Toast.makeText(DemoApplication.this, url, Toast.LENGTH_SHORT).show();
            }
        });

        HNative.getInstance().setWebviewCreator(new WebViewCreator() {
            @Override
            public WebView create(Context context) {
                return new WebView(context);
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
        HNative.getInstance().onDestroy();
    }
}
