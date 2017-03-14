package com.mozz.remoteview;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mozz.remoteview.common.MainHandler;
import com.mozz.remoteview.common.StrRunnable;
import com.mozz.remoteview.common.WefRunnable;
import com.mozz.remoteview.script.Code;
import com.mozz.remoteview.script.LuaRunner;
import com.mozz.remoteview.script.logcat;
import com.mozz.remoteview.script.properties;
import com.mozz.remoteview.script.setParams;
import com.mozz.remoteview.script.toast;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author Yang Tao, 17/3/6.
 */
final class ViewContextImpl implements RViewContext {

    private static boolean DEBUG = false;

    private static final String TAG = RViewContext.class.getSimpleName();

    private static final int ViewContextTag = 0x3 << 24;

    private final Map<String, View> mViewSelector = new ArrayMap<>();

    private final VariablePoolImpl mPool = new VariablePoolImpl();

    private Globals mGlobals;

    private final RVModule mModule;

    private final Context mContext;

    private ViewContextImpl(RVModule module, Context context) {
        mModule = module;
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
    public View put(String id, View value) {
        View before = mViewSelector.put(id, value);
        if (before != null) {
            Log.w(TAG, "Duplicated id " + id + ", before is " + before + ", current is " + value);
        }
        return before;
    }

    @Nullable
    public View findViewById(@NonNull String id) {
        return mViewSelector.get(id);
    }

    public boolean containsView(String id) {
        return mViewSelector.containsKey(id);
    }

    public void onViewLoaded() {

        callCreated();

    }

    public void onViewCreate() {
        // if there is script code in layout file, then initLuaRunner
        if (mModule.mHasScriptEmbed) {
            initLuaRunner();
        }

        initVariablePool();

        callCreate();
    }

    private void initVariablePool() {

    }

    private void callCreate() {
        Code create = mModule.retrieveReserved(FunctionTable.CREATE);
        if (create == null) return;
        execute(create);
    }

    private void callCreated() {
        Code created = mModule.retrieveReserved(FunctionTable.CREATED);
        if (created == null) return;
        execute(created);
    }

    private void initLuaRunner() {
        LuaRunner.getInstance().runLuaScript(new WefRunnable<RViewContext>(this) {
            @Override
            protected void runOverride(@Nullable RViewContext RViewContext) {
                if (RViewContext == null) return;
                long time1 = SystemClock.currentThreadTimeMillis();
                mGlobals = LuaRunner.newGlobals();
                mGlobals.set("view", new setParams(RViewContext));
                mGlobals.set("toast", new toast(RViewContext.getAndroidContext()));
                mGlobals.set("property", new properties.property(RViewContext));
                mGlobals.set("setProperty", new properties.setProperty(RViewContext));
                mGlobals.set("getProperty", new properties.getProperty(RViewContext));
                mGlobals.set("log", new logcat());
                Log.i(TAG, "init Lua module spend " + (SystemClock.currentThreadTimeMillis() - time1) + " ms");
            }
        });

    }

    public String allIdTag() {
        return mViewSelector.toString();
    }

    public static RViewContext getViewContext(@NonNull FrameLayout v) {
        Object object = v.getTag(ViewContextTag);

        if (object != null && object instanceof RViewContext) {
            return (RViewContext) object;
        }

        return null;
    }


    private void execute(@NonNull Code code) {
        execute(code.toString());
    }

    @Override
    public void execute(final String script) {
        if (mGlobals == null) {
            Log.d(TAG, "skip the script \"" + script + "\" because no script in module " + mModule);
            return;
        }

        LuaRunner.getInstance().runLuaScript(new StrRunnableContext(this, script) {
            @Override
            protected void runOverride(String s) {
                ViewContextImpl context = mContextRef.get();
                if (context == null) return;

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
            if (DEBUG)
                MainHandler.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getAndroidContext(), "LuaScript Wrong:\n" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @NonNull
    static RViewContext initViewContext(@NonNull FrameLayout layout, RVModule module, Context context) {
        RViewContext v = new ViewContextImpl(module, context);
        layout.setTag(ViewContextTag, v);
        return v;
    }

    private static abstract class StrRunnableContext extends StrRunnable<String> {

        WeakReference<ViewContextImpl> mContextRef;

        StrRunnableContext(ViewContextImpl context, String s) {
            super(s);

            mContextRef = new WeakReference<>(context);
        }
    }
}
