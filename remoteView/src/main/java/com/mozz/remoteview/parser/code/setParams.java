package com.mozz.remoteview.parser.code;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.mozz.remoteview.parser.ViewContext;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

/**
 * Created by Yang Tao on 17/2/27.
 */
public class setParams extends ThreeArgFunction {

    private ViewContext viewContext;

    public setParams(ViewContext viewContext) {
        this.viewContext = viewContext;
    }

    @Override
    public LuaValue call(LuaValue luaValue, LuaValue luaValue2, LuaValue luaValue3) {
        try {
            String id = luaValue.tojstring();

            int color = Color.parseColor(luaValue2.tojstring());

            View v = viewContext.findViewById(id);
            if (v == null) return LuaValue.NIL;

            if (v instanceof TextView) {
                ((TextView) v).setTextColor(color);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return LuaValue.NIL;
    }
}
