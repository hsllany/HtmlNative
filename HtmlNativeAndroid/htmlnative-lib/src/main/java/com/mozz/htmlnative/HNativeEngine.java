package com.mozz.htmlnative;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.common.ContextProvider;
import com.mozz.htmlnative.css.stylehandler.StyleHandlerFactory;
import com.mozz.htmlnative.dom.HNHead;
import com.mozz.htmlnative.http.HNHttpClient;
import com.mozz.htmlnative.script.ScriptLib;
import com.mozz.htmlnative.script.ScriptRunner;
import com.mozz.htmlnative.utils.ParametersUtils;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/21.
 */

public final class HNativeEngine {

    private static HNConfig sConfig;
    private volatile static boolean sInit = false;

    private HNativeEngine() {
        HNInternalThread.init();
        HNScriptRunnerThread.init();
    }

    @Nullable
    private static HNativeEngine sInstance = null;

    public static void init(Application application, HNConfig config) {

        ContextProvider.install(application);
        initScreenMetrics(application);
        if (config == null) {
            throw new IllegalArgumentException("Config can't be null.");
        }
        sConfig = config;
        sConfig.install();

        sInit = true;
    }

    @Nullable
    public static HNativeEngine getInstance() {
        if (!sInit) {
            throw new IllegalStateException("You must call init() first");
        }
        if (sInstance == null) {
            synchronized (HNativeEngine.class) {
                if (sInstance == null) {
                    sInstance = new HNativeEngine();
                }
            }
        }

        return sInstance;
    }

    public void debugParseProcess() {
        HNLog.setDebugLevel(HNLog.LEXER);
        HNLog.setDebugLevel(HNLog.PARSER);
        HNLog.setDebugLevel(HNLog.CSS_PARSER);
    }

    public void debugRenderProcess() {
        HNLog.setDebugLevel(HNLog.RENDER);
        HNLog.setDebugLevel(HNLog.STYLE);
        HNLog.setDebugLevel(HNLog.PROCESS_THREAD);
    }

    private static void initScreenMetrics(@NonNull Context context) {
        ParametersUtils.init(context);
    }

    public final void loadView(final Context context, final InputStream inputStream, final
    OnHNViewLoaded onHNViewLoaded) {
        HNRenderThread.runRenderTask(new HNRenderThread.RenderTask(context, inputStream,
                onHNViewLoaded));
    }

    public void loadView(Context context, InputStream inputStream, final Activity activity) {
        loadView(context, inputStream, new OnHNViewLoadedWeak<Activity>(activity) {
            @Override
            public void onViewLoaded(@Nullable View v) {
                Activity act = mWeakRef.get();
                if (act != null && !act.isDestroyed() && v != null) {
                    act.setContentView(v);
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onHead(HNHead head) {
            }
        });
    }

    public void loadView(Context context, InputStream inputStream, final ViewGroup viewGroup) {
        loadView(context, inputStream, new OnHNViewLoadedWeak<ViewGroup>(viewGroup) {
            @Override
            public void onViewLoaded(View v) {
                ViewGroup vv = mWeakRef.get();
                vv.addView(v);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onHead(HNHead head) {

            }
        });
    }

    public static String version() {
        return HNEnvironment.v;
    }

    public static int versionCode() {
        return HNEnvironment.versionCode;
    }

    /**
     * @param tag
     * @param HNViewItem
     */
    public static void registerHNiew(String tag, @NonNull HNViewItem HNViewItem) {
        ViewTypeRelations.registerExtraView(tag, HNViewItem);
    }

    public void destroy() {
        HNSegment.clearCache();
        HNInternalThread.quit();
        HNScriptRunnerThread.quit();
        StyleHandlerFactory.clear();
    }


    public static ImageFetcher getImageViewAdapter() {
        return sConfig.getImageViewAdapter();
    }

    public static void registerViewFactory(String androidViewClassName, ViewFactory viewFactory) {
        HNRenderer.registerViewFactory(androidViewClassName, viewFactory);
    }


    public static onHrefClick getHrefLinkHandler() {
        return sConfig.getHrefLinkHandler();
    }

    public static void registerScriptCallback(ScriptCallback callback) {
        HNScriptRunnerThread.setErrorCallback(callback);
    }


    public static HNHttpClient getHttpClient() {
        return sConfig.getHttpClient();
    }

    public static final void registerScriptLib(ScriptLib lib) {
        ScriptRunner.registerLib(lib);
    }

    public interface OnHNViewLoaded {
        void onViewLoaded(View v);

        void onError(Exception e);

        void onHead(HNHead head);
    }

    private abstract class OnHNViewLoadedWeak<T> implements OnHNViewLoaded {
        protected WeakReference<T> mWeakRef;

        public OnHNViewLoadedWeak(T tt) {
            this.mWeakRef = new WeakReference<>(tt);
        }
    }


}
