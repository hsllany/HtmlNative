package com.mozz.htmlnative;

import org.junit.Test;

/**
 * Created by Yang Tao on 17/2/24.
 */
public class ScriptTest {

    private static String luaScript = " print(LuaViewVersion())";
    private static String helloWorldScript = "print('hello world', LuaViewVersion())";

    @Test
    public void execute() throws Exception {

        String a = "hello world";

        System.out.println(CharSequence.class.isAssignableFrom(String.class));
    }


}