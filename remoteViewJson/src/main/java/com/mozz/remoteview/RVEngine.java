package com.mozz.remoteview;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class RVEngine {

    private RVEngine() {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load("print 'hello, world'");
        chunk.invoke();
    }

    private static RVEngine sInstance = null;

    public static RVEngine getInstance() {
        if (sInstance == null) {
            synchronized (RVEngine.class) {
                if (sInstance == null) {
                    sInstance = new RVEngine();
                }
            }
        }

        return sInstance;
    }
}
