package com.mozz.htmlnative.script.lua;

import android.view.View;

import com.mozz.htmlnative.HNSandBoxContext;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

/**
 * @author Yang Tao, 17/3/23.
 */

class LFindViewById extends OneArgFunction implements ILApi {
    private HNSandBoxContext mContext;

    public LFindViewById(HNSandBoxContext context) {
        mContext = context;
    }

    @Override
    public LuaValue call(LuaValue arg) {

        String id = arg.tojstring();
        View v = mContext.findViewById(id);
        if (v != null) {
            LView lView = new LView(v, mContext);
            lView.mAdded = true;
            return lView;
        }

        return NIL;
    }

    @Override
    public String apiName() {
        return "getElementById";
    }
}
