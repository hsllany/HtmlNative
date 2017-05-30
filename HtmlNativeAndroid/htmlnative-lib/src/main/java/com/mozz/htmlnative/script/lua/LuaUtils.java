package com.mozz.htmlnative.script.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Tao, 17/5/30.
 */

final class LuaUtils {
    private LuaUtils() {
    }


    static Map<String, String> luaTableToMap(LuaTable value) {
        Map<String, String> params = new HashMap<>();

        LuaValue[] keys = value.keys();

        for (LuaValue k : keys) {
            if (k.isstring()) {
                LuaValue v = value.get(k);
                if (v.isstring()) {
                    params.put(k.tojstring(), v.tojstring());
                }
            }
        }

        return params;
    }

    static boolean notNull(LuaValue... values) {
        for (LuaValue v : values) {
            if (v == LuaValue.NIL) {
                return false;
            }
        }

        return true;
    }
}
