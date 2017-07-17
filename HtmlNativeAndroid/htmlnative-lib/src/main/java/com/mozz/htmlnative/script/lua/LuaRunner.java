package com.mozz.htmlnative.script.lua;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.script.Lauguage;
import com.mozz.htmlnative.script.ScriptRunner;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * @author Yang Tao, 17/3/21.
 */

@Lauguage(type = "lua")
public class LuaRunner extends ScriptRunner {
    private Globals mGlobals;

    private static final String TAG = LuaRunner.class.getSimpleName();

    public LuaRunner() {
        mGlobals = JsePlatform.standardGlobals();
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

    @Override
    public void onLoad() {
        // register global variables
        register(new LDocument(getSandboxContext()));
        register(LConsole.getInstance());
        register(new LHttp(this));
        // register api
        register(new LToast(getSandboxContext().getAndroidContext()));
    }

    @Override
    public void onUnload() {

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

    final Globals getGlobals() {
        return mGlobals;
    }


}
