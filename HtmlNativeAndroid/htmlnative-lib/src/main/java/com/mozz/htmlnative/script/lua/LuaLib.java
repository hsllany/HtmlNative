package com.mozz.htmlnative.script.lua;

import com.mozz.htmlnative.script.ScriptLib;

import org.luaj.vm2.LuaTable;

/**
 * @author Yang Tao, 17/6/5.
 */

public abstract class LuaLib extends LuaTable implements ScriptLib {
    @Override
    public abstract String libName();
}
