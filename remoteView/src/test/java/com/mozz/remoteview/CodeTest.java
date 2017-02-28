package com.mozz.remoteview;

import com.mozz.remoteview.parser.code.LuaRunner;

import org.junit.Test;
import org.luaj.vm2.LuaValue;

/**
 * Created by Yang Tao on 17/2/24.
 */
public class CodeTest {

    private static String luaScript = " print(LuaViewVersion())";
    private static String helloWorldScript = "print('hello world', LuaViewVersion())";

    @Test
    public void execute() throws Exception {
    }


}