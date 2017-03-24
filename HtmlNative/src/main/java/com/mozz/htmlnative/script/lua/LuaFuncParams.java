package com.mozz.htmlnative.script.lua;

import com.mozz.htmlnative.script.FuncParams;

import org.luaj.vm2.LuaValue;

/**
 * @author Yang Tao, 17/3/24.
 */

public class LuaFuncParams implements FuncParams {
    LuaValue mValue;

    public LuaFuncParams(LuaValue value) {
        mValue = value;
    }
}
