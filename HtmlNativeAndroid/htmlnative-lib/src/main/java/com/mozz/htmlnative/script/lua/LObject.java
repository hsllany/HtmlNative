package com.mozz.htmlnative.script.lua;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * @author Yang Tao, 17/5/31.
 */

abstract class LObject extends LuaTable {
    
    LObject() {
        super();
        set("type", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                String className = LObject.this.onObjectClassName();
                if (className != null) {
                    return LuaString.valueOf(className);
                } else {
                    return LuaValue.NIL;
                }
            }
        });
    }

    abstract String onObjectClassName();
}
