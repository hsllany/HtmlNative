package com.mozz.remoteview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.remoteview.common.Performance;
import com.mozz.remoteview.common.PerformanceWatcher;
import com.mozz.remoteview.reader.StringCodeReader;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Yang Tao on 17/2/22.
 */

public class RemoteViewLoader implements Runnable {
    private String mUrl;

    private Context mContext;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private OnViewLoaded mCallback;

    public RemoteViewLoader(Context context, String url) {
        mUrl = url;
        mContext = context;
    }

    public void load(OnViewLoaded callback) {
        mCallback = callback;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        Response r = null;
        try {
            r = okHttpClient.newCall(request).execute();
            String code = r.body().string();
            Log.d("TestDemo", code);

            PerformanceWatcher clock = Performance.newWatcher();

            Parser p = new Parser(new StringCodeReader(code));
            RVModule rvModule = p.process();

            clock.check("Loader_Parser");

            final View view;
            try {
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                view = RVRenderer.get().inflate(mContext, rvModule, null, false, layoutParams);
                clock.check("Loader_RVInflater");
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null)
                            mCallback.onViewLoaded(view);
                    }
                });
                clock.checkDone("Loader_RenderViewFinished");
            } catch (RVRenderer.RemoteInflateException e) {
                e.printStackTrace();
            }


        } catch (IOException | RVSyntaxError e) {
            e.printStackTrace();
        }
    }
}
