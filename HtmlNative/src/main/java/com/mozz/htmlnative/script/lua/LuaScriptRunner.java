package com.mozz.htmlnative.script.lua;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.mozz.htmlnative.EventLog;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.common.MainHandler;
import com.mozz.htmlnative.script.ScriptRunner;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * @author Yang Tao, 17/3/21.
 */

public class LuaScriptRunner extends ScriptRunner {
    private Globals mGlobals;

    private static final String TAG = LuaScriptRunner.class.getSimpleName();

    public LuaScriptRunner(HNSandBoxContext sandBoxContext) {
        super(sandBoxContext);
        long time1 = SystemClock.currentThreadTimeMillis();
        mGlobals = JsePlatform.standardGlobals();
        mGlobals.set("view", new setParams(sandBoxContext));
        mGlobals.set("toast", new toast(sandBoxContext.getAndroidContext()));
        mGlobals.set("property", new properties.property(sandBoxContext));
        mGlobals.set("setProperty", new properties.setProperty(sandBoxContext));
        mGlobals.set("getProperty", new properties.getProperty(sandBoxContext));
        mGlobals.set("log", new logcat());
        Log.i(TAG, "init Lua module spend " + (SystemClock.currentThreadTimeMillis() - time1) + "" +
                " ms");
    }

    @Override
    public void run(String script) {
        EventLog.writeEvent(EventLog.TAG_VIEW_CONTEXT, "Execute script \"" + script + "\".");
        try {
            LuaValue l = mGlobals.load(script);
            l.call();
        } catch (final LuaError e) {
            // make sure that lua script dose not crash the whole app
            e.printStackTrace();
            Log.e(TAG, "LuaScriptRun");

            MainHandler.instance().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mSandbox.getAndroidContext(), "LuaScript Wrong:\n" + e
                            .getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
