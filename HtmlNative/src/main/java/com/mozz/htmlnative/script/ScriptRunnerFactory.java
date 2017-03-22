package com.mozz.htmlnative.script;

import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.script.lua.LuaRunner;

/**
 * @author Yang Tao, 17/3/21.
 */

public class ScriptRunnerFactory {

    public static ScriptRunner createRunner(int type, HNSandBoxContext context) {
        if (type == ScriptInfo.SCRIPT_LUA) {
            return new LuaRunner(context);
        } else {
            return null;
        }
    }
}
