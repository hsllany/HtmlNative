package com.mozz.htmlnative.script.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * @author Yang Tao, 17/3/21.
 */

public class LCallback extends TwoArgFunction implements ILApi {
    private LuaRunner mRunner;

    public LCallback(LuaRunner runner) {
        mRunner = runner;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        if (!arg2.eq_b(LuaValue.NIL)) {
            String funName = arg1.tojstring();
            mRunner.putFunction(funName, arg2);
        }
        return LuaValue.NIL;

    }

    @Override
    public String apiName() {
        return "register";
    }
}
