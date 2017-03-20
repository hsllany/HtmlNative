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

import com.mozz.htmlnative.script.LuaRunner;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/21.
 */

public final class HNative {

    public static final String LUA_TAG = "RVScript";

    private static final String TAG = "HNative";

    private DisplayMetrics mDefaultMetrics;

    private HNative() {
        ProcessThread.init();
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
    OnRViewLoaded onRViewLoaded) {
        ProcessThread.runRenderTask(new ProcessThread.RenderTask(context, inputStream,
                onRViewLoaded));
    }

    public void loadView(Context context, InputStream inputStream, Activity activity) {
        loadView(context, inputStream, new OnRViewLoadedWeak<Activity>(activity) {
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
        });
    }

    public void loadView(Context context, InputStream inputStream, final ViewGroup viewGroup) {
        loadView(context, inputStream, new OnRViewLoadedWeak<ViewGroup>(viewGroup) {
            @Override
            public void onViewLoaded(View v) {
                ViewGroup vv = mWeakRef.get();
                vv.addView(v);
            }

            @Override
            public void onError(Exception e) {

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
     * @param HNviewItem
     */
    public static void registerRView(String tag, @NonNull HNviewItem HNviewItem) {
        ViewTagLookupTable.registerExtraView(tag, HNviewItem);
    }

    public void onDestroy() {
        HNSegment.clearCache();
        ProcessThread.quit();
        LuaRunner.getInstance().quit();
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

    public interface OnRViewLoaded {
        void onViewLoaded(View v);

        void onError(Exception e);
    }

    private abstract class OnRViewLoadedWeak<T> implements OnRViewLoaded {
        protected WeakReference<T> mWeakRef;

        public OnRViewLoadedWeak(T tt) {
            this.mWeakRef = new WeakReference<>(tt);
        }
    }
}
