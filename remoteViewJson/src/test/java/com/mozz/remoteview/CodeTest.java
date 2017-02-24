package com.mozz.remoteview;

import com.mozz.remoteview.parser.code.LuaRunner;

import org.junit.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import static org.junit.Assert.*;

/**
 * Created by Yang Tao on 17/2/24.
 */
public class CodeTest {

    private static String luaScript = " print(LuaViewVersion())";
    private static String helloWorldScript = "print('hello world', LuaViewVersion())";

    @Test
    public void execute() throws Exception {
        LuaValue l = LuaRunner.getInstance().run(luaScript);
        l.call();

        LuaValue l2 = LuaRunner.getInstance().run(helloWorldScript);
        l2.call();
    }


}