package com.mozz.htmlnative;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/21.
 */

public final class HNative {

    public static final String LUA_TAG = "HNativeLog";

    private static final String TAG = "HNative";

    private DisplayMetrics mDefaultMetrics;

    private HNative() {
        HNProcessThread.init();
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

    DisplayMetrics getScreenMetrics() {
        return mDefaultMetrics;
    }

    private void initScreenMetrics(@NonNull Context context) {
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        mDefaultMetrics = new DisplayMetrics();
        display.getMetrics(mDefaultMetrics);
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
    public static void registerRView(String tag, @NonNull HNViewItem HNViewItem) {
        ViewTagLookupTable.registerExtraView(tag, HNViewItem);
    }

    public void onDestroy() {
        HNSegment.clearCache();
        HNProcessThread.quit();
        HNScriptRunnerThread.quit();
    }

    public void setImageViewAdapter(@NonNull ImageViewAdapter adapter) {
        HNRenderer.setImageViewAdapter(adapter);
    }

    public void setWebviewCreator(@NonNull WebViewCreator creator) {
        HNRenderer.setWebViewCreator(creator);
    }

    public void setHrefLinkHandler(@NonNull HrefLinkHandler handler) {
        HNRenderer.setHrefLinkHandler(handler);
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
