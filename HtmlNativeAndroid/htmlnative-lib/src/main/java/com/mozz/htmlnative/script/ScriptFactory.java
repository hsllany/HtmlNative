package com.mozz.htmlnative.script;

import android.util.ArrayMap;

import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.script.lua.LuaRunner;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/21.
 */

public final class ScriptFactory {

    private ScriptFactory() {
    }

    private static Map<String, Integer> sSupportedScriptType = new ArrayMap<>();

    public static final int JAVASCRIPT = 0x01;
    public static final int LUA = 0x02;

    static {
        sSupportedScriptType.put("JavaScript", JAVASCRIPT);
        sSupportedScriptType.put("Lua", LUA);
    }

    public static ScriptRunner createRunner(int type, HNSandBoxContext context) {
        if (type == LUA) {
            return new LuaRunner(context);
        } else {
            return null;
        }
    }

    public static int typeOf(String scriptName) {
        return sSupportedScriptType.get(scriptName);
    }

    public static String nameOf(int type) {

        for (Map.Entry<String, Integer> entry : sSupportedScriptType.entrySet()) {
            if (entry.getValue() == type) {
                return entry.getKey();
            }
        }

        return null;
    }
}
