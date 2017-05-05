package com.mozz.htmlnative;

import org.junit.Test;
import org.luaj.vm2.LuaValue;

/**
 * @author Yang Tao, 17/3/2.
 */

public class LuaValueTest {

    @Test
    public void testString() {
        System.out.println("\ntestString");
        LuaValue value = LuaValue.valueOf("hello world");
        println(value);
    }

    @Test
    public void testInt() {
        System.out.println("\ntestInt");
        LuaValue value = LuaValue.valueOf(3);
        println(value);
    }

    private void println(LuaValue value) {
        System.out.println(value.tojstring());
        System.out.println(value.toboolean());
        System.out.println(value.toint());
        System.out.println(value.todouble());
        System.out.println(value.tofloat());
    }
}
