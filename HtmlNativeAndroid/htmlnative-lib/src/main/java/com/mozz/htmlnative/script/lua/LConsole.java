package com.mozz.htmlnative.script.lua;

import android.util.Log;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

/**
 * @author Yang Tao, 17/2/28.
 */

public class LConsole extends LuaTable implements ILGlobalObject {

    private static final String LUA_TAG = "HNLua";

    private static LConsole sInstance = null;

    public static LConsole getInstance() {
        if (sInstance == null) {
            sInstance = new LConsole();
        }

        return sInstance;
    }

    private LConsole() {
        set("log", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Log.d(LUA_TAG, arg.tojstring());
                return LuaValue.NIL;
            }
        });

        set("info", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Log.i(LUA_TAG, arg.tojstring());
                return LuaValue.NIL;
            }
        });

        set("error", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Log.e(LUA_TAG, arg.tojstring());
                return LuaValue.NIL;
            }
        });

        set("warn", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Log.i(LUA_TAG, arg.tojstring());
                return LuaValue.NIL;
            }
        });
    }

    @Override
    public String objectName() {
        return "console";
    }
}
