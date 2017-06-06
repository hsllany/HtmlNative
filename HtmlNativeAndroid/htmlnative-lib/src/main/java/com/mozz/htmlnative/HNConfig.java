package com.mozz.htmlnative;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;

import com.mozz.htmlnative.http.HNHttpClient;
import com.mozz.htmlnative.script.lua.EmptyHttpClient;
import com.mozz.htmlnative.view.BackgroundViewDelegate;

/**
 * @author Yang Tao, 17/6/6.
 */

public class HNConfig {
    static {
        HNRenderer.registerViewFactory(WebView.class.getName(), DefaultWebViewFactory.sInstance);
    }

    private ImageFetcher mImageFetcher = DefaultImageAdapter.sInstance;
    private onHrefClick mOnHrefClick = DefaultOnHrefClick.sInstance;
    private HNHttpClient mHttpClient = EmptyHttpClient.instance;
    private ScriptCallback mScriptCallback;

    public ImageFetcher getImageViewAdapter() {
        return mImageFetcher;
    }

    public onHrefClick getHrefLinkHandler() {
        return mOnHrefClick;
    }

    void install() {
        HNScriptRunnerThread.setErrorCallback(mScriptCallback);
    }

    public HNHttpClient getHttpClient() {
        return mHttpClient;
    }

    /**
     * @author Yang Tao, 17/3/11.
     */

    private static final class DefaultOnHrefClick implements onHrefClick {

        @NonNull
        static final DefaultOnHrefClick sInstance;

        static {
            sInstance = new DefaultOnHrefClick();
        }

        @Override
        public void onHref(String url, @NonNull View view) {
            Intent intent = new Intent();
            intent.setAction("Android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * @author Yang Tao, 17/3/10.
     */

    private static final class DefaultImageAdapter implements ImageFetcher {

        static DefaultImageAdapter sInstance;

        static {
            sInstance = new DefaultImageAdapter();
        }

        private DefaultImageAdapter() {
        }

        @Override
        public void setImage(String src, BackgroundViewDelegate imageView) {
            //do nothing
        }
    }

    /**
     * @author Yang Tao, 17/3/8.
     */

    private static final class DefaultWebViewFactory implements WebViewFactory {

        static DefaultWebViewFactory sInstance;

        static {
            sInstance = new DefaultWebViewFactory();
        }


        @NonNull
        @Override
        public WebView create(Context context) {
            return new WebView(context);
        }
    }

    public static class Builder {
        private HNConfig sConfig;

        public Builder() {
            sConfig = new HNConfig();
        }

        public Builder setImageFetcher(ImageFetcher adapter) {
            if (adapter != null) {
                sConfig.mImageFetcher = adapter;
            }
            return this;
        }

        public Builder setOnHrefClick(onHrefClick onHrefClick) {
            if (onHrefClick != null) {
                sConfig.mOnHrefClick = onHrefClick;
            }
            return this;
        }

        public Builder setHttpClient(HNHttpClient client) {
            if (client != null) {
                sConfig.mHttpClient = client;
            }
            return this;
        }

        public Builder setScriptCallback(ScriptCallback scriptCallback) {
            if (scriptCallback != null) {
                sConfig.mScriptCallback = scriptCallback;
            }

            return this;
        }

        public HNConfig build() {
            return sConfig;
        }
    }
}
