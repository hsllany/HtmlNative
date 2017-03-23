package com.mozz.htmlnative.script.lua;

import android.view.View;

import com.mozz.htmlnative.HNSandBoxContext;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

/**
 * @author Yang Tao, 17/3/23.
 */

public class ViewBy extends OneArgFunction {
    private HNSandBoxContext mContext;

    public ViewBy(HNSandBoxContext context) {
        mContext = context;
    }

    @Override
    public LuaValue call(LuaValue arg) {

        String id = arg.tojstring();
        View v = mContext.findViewById(id);
        if (v != null) {
            return new LuaView(v, mContext);
        }

        return NIL;
    }
}
