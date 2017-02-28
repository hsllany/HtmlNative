package com.mozz.remoteview.parser.code;

import android.util.Log;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import static com.mozz.remoteview.RVEngine.LUA_TAG;

/**
 * @author Yang Tao, 17/2/28.
 */

public class logcat extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue luaValue) {

        if (luaValue.isstring()) {
            Log.d(LUA_TAG, luaValue.tojstring());
        }
        return LuaValue.NIL;
    }
}
