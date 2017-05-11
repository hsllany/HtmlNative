package com.mozz.htmlnative;

import org.junit.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * @author Yang Tao, 17/2/24.
 */
public class ScriptTest {

    private static String luaScript = " print(LuaViewVersion())";
    private static String helloWorldScript = "myfunction = function()\n" +
            "print(\"hello world\")\n" +
            "end\n" +
            "\n" +
            "";

    @Test
    public void execute() throws Exception {
        Globals globals = JsePlatform.standardGlobals();
        //        globals.set("LCallback", new LCallback());

        LuaValue value = globals.load(helloWorldScript);
        value.call();
    }


}