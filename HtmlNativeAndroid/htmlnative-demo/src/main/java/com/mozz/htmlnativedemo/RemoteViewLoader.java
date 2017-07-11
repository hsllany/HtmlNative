package com.mozz.htmlnativedemo;

import android.content.Context;

import com.mozz.htmlnative.HNativeEngine;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Yang Tao on 17/2/22.
 */

public class RemoteViewLoader {

    private static OkHttpClient sOkHttpClient = new OkHttpClient();
    private Context mContext;

    public RemoteViewLoader(Context context) {
        mContext = context;
    }

    public void load(final String url, final HNativeEngine.OnHNViewLoaded callback) {
        Request request = new Request.Builder().url(url).build();
        sOkHttpClient.newCall(request).enqueue(new HttpCallback(mContext) {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Context context = mContextWekRef.get();
                if (context != null) {
                    HNativeEngine.getInstance().loadView(mContext, response.body().byteStream(),
                            callback);

                } else {
                    if (callback != null) {
                        callback.onError(null);
                    }
                }
            }
        });
    }

    private abstract static class HttpCallback implements Callback {

        protected WeakReference<Context> mContextWekRef;

        public HttpCallback(Context context) {
            mContextWekRef = new WeakReference<Context>(context);
        }
    }


}
