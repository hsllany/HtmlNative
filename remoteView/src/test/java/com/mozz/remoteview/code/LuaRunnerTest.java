package com.mozz.remoteview.code;

import org.junit.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Created by Yang Tao on 17/2/24.
 */
public class LuaRunnerTest {
    private static String luaScript = " print(3)" +
            "\n " +
            "result = {}" +
            "\n" +
            "result['instance'] = 3" +
            "\n" +
            "return result";
    private static String helloWorldScript = "print('hello world', hsl, LuaViewVersion())";

    @Test
    public void testLuaRunner() {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue value = globals.load(luaScript);
        Varargs varargs = value.invoke();


        LuaTable value1 = (LuaTable) varargs.arg1();
        System.out.println(value1.keyCount());
        System.out.println(value1);

        System.out.println(value1.get("instance"));
        System.out.println(value.getmetatable());
    }

}