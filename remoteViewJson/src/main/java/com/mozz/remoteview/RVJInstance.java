package com.mozz.remoteview;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class RVJInstance {

    private RVJInstance() {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load("print 'hello, world'");
        chunk.invoke();
    }

    private static RVJInstance sInstance = null;

    public static RVJInstance getInstance() {
        if (sInstance == null) {
            synchronized (RVJInstance.class) {
                if (sInstance == null) {
                    sInstance = new RVJInstance();
                }
            }
        }

        return sInstance;
    }
}
