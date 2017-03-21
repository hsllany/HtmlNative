package com.mozz.htmlnativedemo;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.mozz.htmlnative.HNative;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Yang Tao on 17/2/22.
 */

public class RemoteViewLoader {

    private HandlerThread mThread = new HandlerThread("Running");
    private Handler mHandler;

    private Context mContext;

    public RemoteViewLoader(Context context) {
        mContext = context;

        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    public void load(final String url, final HNative.OnHNViewLoaded callback) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();

                Response r = null;
                try {
                    r = okHttpClient.newCall(request).execute();
                    HNative.getInstance().loadView(mContext, r.body().byteStream(), callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mHandler.post(r);
    }


}
