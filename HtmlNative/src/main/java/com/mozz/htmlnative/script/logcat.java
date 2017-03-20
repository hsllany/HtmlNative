package com.mozz.htmlnative.script;

import android.support.annotation.NonNull;
import android.util.Log;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import static com.mozz.htmlnative.RV.LUA_TAG;

/**
 * @author Yang Tao, 17/2/28.
 */

public class logcat extends OneArgFunction {
    @Override
    public LuaValue call(@NonNull LuaValue luaValue) {
        Log.d(LUA_TAG, luaValue.tojstring());
        return LuaValue.NIL;
    }
}
