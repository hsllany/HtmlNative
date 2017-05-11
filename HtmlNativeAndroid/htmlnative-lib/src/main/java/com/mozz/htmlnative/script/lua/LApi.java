package com.mozz.htmlnative.script.lua;

/**
 * One implements {@link LApi} must also be a sub-class of {@link org.luaj.vm2.LuaValue}
 *
 * @author Yang Tao, 17/5/11.
 */
interface LApi {
    String apiName();
}
