package com.mozz.htmlnative.script;

import com.mozz.htmlnative.utils.Utils;

import org.junit.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;

/**
 * Created by Yang Tao on 17/2/24.
 */
public class LuaRunnerTest {

    @Test
    public void testLuaRunner() {
        String luaScript = Utils.toString(new File("lua_test/test.lua"));
        Globals globals = JsePlatform.standardGlobals();
        LuaValue value = globals.load(luaScript);
        value.invoke();
    }

    private static class TestObj extends LuaUserdata{

        public TestObj(Object obj) {
            super(obj);
        }

        public TestObj(Object obj, LuaValue metatable) {
            super(obj, metatable);
        }
    }

}