package com.mozz.htmlnative;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.attrs.AttrsHelper;
import com.mozz.htmlnative.common.Utils;
import com.mozz.htmlnative.dom.HNHead;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import static com.mozz.htmlnative.HNLog.STYLE;

/**
 * @author Yang Tao, 17/2/21.
 */

public final class HNative {

    public static final String LUA_TAG = "HNLua";

    private static WebViewCreator sWebViewHandler = DefaultWebViewCreator.sInstance;
    private static ImageViewAdapter sImageViewAdapter = DefaultImageAdapter.sInstance;
    private static HrefLinkHandler sHrefLinkHandler = DefaultHrefLinkHandler.sInstance;

    private HNative() {
        HNInternalThread.init();
        HNScriptRunnerThread.init();
    }

    @Nullable
    private static HNative sInstance = null;

    @Nullable
    public static HNative getInstance() {
        if (sInstance == null) {
            synchronized (HNative.class) {
                if (sInstance == null) {
                    sInstance = new HNative();
                }
            }
        }

        return sInstance;
    }

    public void init(@NonNull Context context) {
        initScreenMetrics(context);
    }

    public void debugAll() {
        HNLog.setDebugLevel(STYLE);
        HNLog.setDebugLevel(HNLog.RENDER);
        HNLog.setDebugLevel(HNLog.ATTR);
    }

    private void initScreenMetrics(@NonNull Context context) {
        Utils.init(context);
    }

    public final void loadView(final Context context, final InputStream inputStream, final
    OnHNViewLoaded onHNViewLoaded) {
        HNProcessThread.runRenderTask(new HNProcessThread.RenderTask(context, inputStream,
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
        ViewRelations.registerExtraView(tag, HNViewItem);
    }

    public void destroy() {
        HNSegment.clearCache();
        HNInternalThread.quit();
        HNScriptRunnerThread.quit();
        AttrsHelper.clear();
    }

    public void setImageViewAdapter(@NonNull ImageViewAdapter adapter) {
        sImageViewAdapter = adapter;
    }

    public static ImageViewAdapter getImageViewAdapter() {
        return sImageViewAdapter;
    }

    public void setWebviewCreator(@NonNull WebViewCreator creator) {
        sWebViewHandler = creator;
    }

    public static WebViewCreator getWebviewCreator() {
        return sWebViewHandler;
    }

    public void setHrefLinkHandler(@NonNull HrefLinkHandler handler) {
        sHrefLinkHandler = handler;
    }

    public static HrefLinkHandler getHrefLinkHandler() {
        return sHrefLinkHandler;
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
