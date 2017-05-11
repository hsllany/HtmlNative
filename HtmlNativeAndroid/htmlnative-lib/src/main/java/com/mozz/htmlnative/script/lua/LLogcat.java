package com.mozz.htmlnative.script.lua;

import android.support.annotation.NonNull;
import android.util.Log;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import static com.mozz.htmlnative.HNativeEngine.LUA_TAG;

/**
 * @author Yang Tao, 17/2/28.
 */

public class LLogcat extends OneArgFunction implements LApi {
    @Override
    public LuaValue call(@NonNull LuaValue luaValue) {
        Log.d(LUA_TAG, luaValue.tojstring());
        return LuaValue.NIL;
    }

    @Override
    public String apiName() {
        return "log";
    }
}
