package com.mozz.htmlnative;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mozz.htmlnative.common.MainHandler;
import com.mozz.htmlnative.common.StrRunnable;
import com.mozz.htmlnative.common.WefRunnable;
import com.mozz.htmlnative.script.LuaRunner;
import com.mozz.htmlnative.script.logcat;
import com.mozz.htmlnative.script.properties;
import com.mozz.htmlnative.script.setParams;
import com.mozz.htmlnative.script.toast;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author Yang Tao, 17/3/6.
 */
final class SandBoxContextImpl implements RVSandBoxContext {

    private static boolean DEBUG = false;

    private static final String TAG = RVSandBoxContext.class.getSimpleName();

    private static final int ViewContextTag = 0x3 << 24;

    private final Map<String, View> mViewWithId = new ArrayMap<>();

    private final VariablePoolImpl mPool = new VariablePoolImpl();

    private Globals mGlobals;

    private final RVSegment mSegment;

    private final Context mContext;

    private SandBoxContextImpl(RVSegment segment, Context context) {
        mSegment = segment;
        mContext = context;
    }

    public Context getAndroidContext() {
        return mContext;
    }

    public void addVariable(String string, Object object) {
        mPool.addVariable(string, object);
    }

    public void updateVariable(String string, Object newValue) {
        mPool.updateVariable(string, newValue);
    }

    public Object getVariable(String string) {
        return mPool.getVariable(string);
    }

    @Nullable
    public View put(String id, View view) {
        View before = mViewWithId.put(id, view);
        if (before != null) {
            Log.w(TAG, "Duplicated id " + id + ", before is " + before + ", current is " + view);
        }
        return before;
    }

    @Nullable
    public View findViewById(@NonNull String id) {
        return mViewWithId.get(id);
    }

    public boolean containsView(String id) {
        return mViewWithId.containsKey(id);
    }

    public void onViewLoaded() {

        callCreated();

    }

    public void onViewCreate() {
        // if there is script code in layout file, then initLuaRunner
        if (mSegment.mHasScriptEmbed) {
            initLuaRunner();
        }

        initVariablePool();

        callCreate();
    }

    private void initVariablePool() {

    }

    private void callCreate() {
        Script create = mSegment.retrieveReserved(ScriptTable.CREATE);
        if (create == null) {
            return;
        }
        execute(create);
    }

    private void callCreated() {
        Script created = mSegment.retrieveReserved(ScriptTable.CREATED);
        if (created == null) {
            return;
        }
        execute(created);
    }

    private void initLuaRunner() {
        LuaRunner.getInstance().runLuaScript(new WefRunnable<RVSandBoxContext>(this) {
            @Override
            protected void runOverride(@Nullable RVSandBoxContext RVSandBoxContext) {
                if (RVSandBoxContext == null) {
                    return;
                }
                long time1 = SystemClock.currentThreadTimeMillis();
                mGlobals = LuaRunner.newGlobals();
                mGlobals.set("view", new setParams(RVSandBoxContext));
                mGlobals.set("toast", new toast(RVSandBoxContext.getAndroidContext()));
                mGlobals.set("property", new properties.property(RVSandBoxContext));
                mGlobals.set("setProperty", new properties.setProperty(RVSandBoxContext));
                mGlobals.set("getProperty", new properties.getProperty(RVSandBoxContext));
                mGlobals.set("log", new logcat());
                Log.i(TAG, "init Lua module spend " + (SystemClock.currentThreadTimeMillis() -
                        time1) + " ms");
            }
        });

    }

    public String allIdTag() {
        return mViewWithId.toString();
    }

    public static RVSandBoxContext getViewContext(@NonNull FrameLayout v) {
        Object object = v.getTag(ViewContextTag);

        if (object != null && object instanceof RVSandBoxContext) {
            return (RVSandBoxContext) object;
        }

        return null;
    }


    private void execute(@NonNull Script script) {
        execute(script.toString());
    }

    @Override
    public void execute(final String script) {
        if (mGlobals == null) {
            Log.d(TAG, "skip the script \"" + script + "\" because no script in module " + mSegment);
            return;
        }

        LuaRunner.getInstance().runLuaScript(new StrRunnableContext(this, script) {
            @Override
            protected void runOverride(String s) {
                SandBoxContextImpl context = mContextRef.get();
                if (context == null) {
                    return;
                }

                context.executeNowWithoutException(s);
            }
        });
    }

    /**
     * Actual method to run script, which swallow the exception to prevent app crash
     *
     * @param s, script to run
     */
    private void executeNowWithoutException(String s) {
        EventLog.writeEvent(EventLog.TAG_VIEW_CONTEXT, "Execute script \"" + s + "\".");
        try {
            LuaValue l = mGlobals.load(s);
            l.call();
        } catch (@NonNull final LuaError e) {
            // make sure that lua script dose not crash the whole app
            e.printStackTrace();
            Log.e(TAG, "LuaScriptRun");
            if (DEBUG) {
                MainHandler.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getAndroidContext(), "LuaScript Wrong:\n" + e.getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @NonNull
    static RVSandBoxContext create(@NonNull FrameLayout layout, RVSegment module, Context context) {
        RVSandBoxContext v = new SandBoxContextImpl(module, context);
        layout.setTag(ViewContextTag, v);
        return v;
    }

    private static abstract class StrRunnableContext extends StrRunnable<String> {

        WeakReference<SandBoxContextImpl> mContextRef;

        StrRunnableContext(SandBoxContextImpl context, String s) {
            super(s);

            mContextRef = new WeakReference<>(context);
        }
    }
}
