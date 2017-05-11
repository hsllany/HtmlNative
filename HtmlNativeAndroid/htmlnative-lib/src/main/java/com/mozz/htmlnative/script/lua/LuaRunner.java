package com.mozz.htmlnative.script.lua;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.util.Log;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.script.ScriptRunner;
import com.mozz.htmlnative.utils.MainHandlerUtils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashMap;
import java.util.Map;

import static com.mozz.htmlnative.HNEnvironment.PERFORMANCE_TAG;

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
        register(new LToast(sandBoxContext.getAndroidContext()));
        register(new LCallback(this));
        register(new LLogcat());
        register(new LFindViewById(sandBoxContext));
        Log.i(PERFORMANCE_TAG, "init Lua module spend " + (SystemClock.currentThreadTimeMillis()
                - time1) + "" +
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
                    new AlertDialog.Builder(mSandbox.getAndroidContext()).setMessage("LuaScript " +
                            "Wrong:\n" + e.getMessage()).setTitle("LuaSyntaxError")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

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

    private void register(LApi api) {
        if (api instanceof LuaValue) {
            mGlobals.set(api.apiName(), (LuaValue) api);
        }
    }

}
