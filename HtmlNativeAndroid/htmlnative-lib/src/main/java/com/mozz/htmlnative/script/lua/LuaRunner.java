package com.mozz.htmlnative.script.lua;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.utils.MainHandlerUtils;
import com.mozz.htmlnative.script.ScriptRunner;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Tao, 17/3/21.
 */

public class LuaRunner extends ScriptRunner {
    private Globals mGlobals;

    private static final String TAG = LuaRunner.class.getSimpleName();

    private Map<String, LuaValue> mFunctionTable = new HashMap<>();

    public LuaRunner(HNSandBoxContext sandBoxContext) {
        super(sandBoxContext);
        long time1 = SystemClock.currentThreadTimeMillis();
        mGlobals = JsePlatform.standardGlobals();
        mGlobals.set("alert", new toast(sandBoxContext.getAndroidContext()));
        mGlobals.set("callback", new callback(this));
        mGlobals.set("log", new logcat());
        mGlobals.set("view", new ViewBy(sandBoxContext));
        Log.i(TAG, "init Lua module spend " + (SystemClock.currentThreadTimeMillis() - time1) + "" +
                " ms");
    }

    @Override
    public void run(String script) {
        HNLog.d(HNLog.SANDBOX, "Execute script \"" + script + "\".");
        try {
            LuaValue l = mGlobals.load(script);
            l.call();
        } catch (final LuaError e) {
            // make sure that lua script dose not crash the whole app
            e.printStackTrace();
            Log.e(TAG, "LuaScriptRun");

            MainHandlerUtils.instance().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mSandbox.getAndroidContext(), "LuaScript Wrong:\n" + e
                            .getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void runFunction(String functionName) {
        LuaValue v = mFunctionTable.get(functionName);
        if (v != null) {
            v.call();
        }
    }

    public void runFunction(String functionName, LuaFuncParams params) {
        LuaValue v = mFunctionTable.get(functionName);
        if (v != null) {
            v.call(params.mValue);
        }
    }

    public void putFunction(String functionName, LuaValue l) {
        mFunctionTable.put(functionName, l);
    }

}
