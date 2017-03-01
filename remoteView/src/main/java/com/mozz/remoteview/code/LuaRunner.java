package com.mozz.remoteview.code;

import android.os.Handler;
import android.os.HandlerThread;

import com.mozz.remoteview.Version;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * @author Yang Tao, 17/2/24.
 */
public final class LuaRunner {

    private HandlerThread mScriptThread = new HandlerThread("RVLuaScriptThread");
    private Handler mHandler;

    private LuaRunner() {
        mScriptThread.start();
        mHandler = new Handler(mScriptThread.getLooper());
    }

    public void quit() {
        mScriptThread.quit();
    }

    public void runLuaScript(Runnable r) {
        mHandler.post(r);
    }

    private static LuaRunner instance = null;

    public static LuaRunner getInstance() {
        if (instance == null) {
            synchronized (LuaRunner.class) {
                if (instance == null)
                    instance = new LuaRunner();
            }
        }

        return instance;
    }


    public static Globals newGlobals() {
        Globals globals = JsePlatform.standardGlobals();
        globals.set("LuaViewVersion", new LuaViewVersion());
        return globals;
    }

    private static class LuaViewVersion extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Version.v);
        }
    }


}
