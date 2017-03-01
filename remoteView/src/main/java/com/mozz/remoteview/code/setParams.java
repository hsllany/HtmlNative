package com.mozz.remoteview.code;

import android.graphics.Color;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mozz.remoteview.ViewContext;
import com.mozz.remoteview.common.MainHandler;

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
    public LuaValue call(final LuaValue luaValue, final LuaValue luaValue2, LuaValue luaValue3) {

        MainHandler.instance().post(new Runnable() {
            @Override
            public void run() {
                try {
                    String id = luaValue.tojstring();

                    View v = viewContext.findViewById(id);
                    if (v == null) return;

                    if (luaValue2.tojstring().equals("false")) {
                        v.setVisibility(View.GONE);
                        Log.d("BUG", v.toString());
                    } else {
                        v.setVisibility(View.VISIBLE);
                    }


                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });


        return LuaValue.NIL;
    }
}
