package com.mozz.htmlnative.script.lua;

import android.os.SystemClock;
import android.util.Log;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.script.ScriptRunner;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import static com.mozz.htmlnative.HNEnvironment.PERFORMANCE_TAG;

/**
 * @author Yang Tao, 17/3/21.
 */

public class LuaRunner extends ScriptRunner {
    private Globals mGlobals;

    private static final String TAG = LuaRunner.class.getSimpleName();

    public LuaRunner(HNSandBoxContext sandBoxContext) {
        super(sandBoxContext);

        long time1 = SystemClock.currentThreadTimeMillis();
        mGlobals = JsePlatform.standardGlobals();

        // register global variables
        register(new LDocument(sandBoxContext));
        register(LConsole.getInstance());

        register(new LHttp(this));

        // register api
        register(new LToast(sandBoxContext.getAndroidContext()));
        Log.i(PERFORMANCE_TAG, "init Lua module spend " + (SystemClock.currentThreadTimeMillis()
                - time1) + "" + " ms");
    }

    @Override
    public void run(String script) {
        HNLog.d(HNLog.SANDBOX, "Execute script \"" + script + "\".");
        LuaValue l = mGlobals.load(script);
        l.call();
    }

    @Override
    public void runFunction(String functionName) {
        LuaValue v = mGlobals.get(functionName);
        if (v != null) {
            v.call();
        }
    }

    protected final void register(ILApi api) {
        if (api instanceof LuaValue) {
            mGlobals.set(api.apiName(), (LuaValue) api);
        }
    }

    protected final void register(ILGlobalObject api) {
        if (api instanceof LuaValue) {
            mGlobals.set(api.objectName(), (LuaValue) api);
        }
    }


}
