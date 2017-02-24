package com.mozz.remoteview.parser.code;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Created by Yang Tao on 17/2/24.
 */

public class LuaRunner {


    public static Globals newGlobals() {
        Globals globals = JsePlatform.standardGlobals();
        globals.set("LuaViewVersion", new LuaViewVersion());
        return globals;
    }

    private static class LuaViewVersion extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return LuaValue.valueOf("LuaViewVersion:1.0");
        }
    }


}
