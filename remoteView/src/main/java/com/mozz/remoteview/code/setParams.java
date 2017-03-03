package com.mozz.remoteview.code;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.mozz.remoteview.ViewContext;
import com.mozz.remoteview.common.MainHandler;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

/**
 * @author Yang Tao, 17/2/27.
 */
public class setParams extends ThreeArgFunction {

    private ViewContext viewContext;

    public setParams(ViewContext viewContext) {
        this.viewContext = viewContext;
    }

    @Override
    public LuaValue call(final LuaValue luaValue, final LuaValue luaValue2, final LuaValue luaValue3) {

        MainHandler.instance().post(new Runnable() {
            @Override
            public void run() {
                try {
                    String id = luaValue.tojstring();

                    View v = viewContext.findViewById(id);
                    if (v == null)
                        throw new ParamsWrongException("can't find related view by id:" + id);

                    String property = luaValue2.tojstring();

                    changeViewProperty(v, property, luaValue3);


                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (ParamsWrongException e) {
                    e.printStackTrace();
                }
            }
        });

        return LuaValue.NIL;
    }

    private static void changeViewProperty(View v, String property, LuaValue value)
            throws ParamsWrongException {
        switch (property) {
            case "visible":
                boolean visible = value.toboolean();
                v.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                break;

            case "text":
                String text = value.tojstring();
                if (v instanceof TextView) {
                    ((TextView) v).setText(text);
                } else {
                    throw new ParamsWrongException("not TextView or its subclass");
                }
                break;

            case "background":
                String colorStr = value.tojstring();
                try {
                    int color = Color.parseColor(colorStr);
                    v.setBackgroundColor(color);
                } catch (Exception e) {
                    ParamsWrongException paramsWrongException =
                            new ParamsWrongException("wrong with color parsing");
                    paramsWrongException.initCause(e);
                    throw paramsWrongException;

                }
                break;
        }
    }

    private static class ParamsWrongException extends Exception {
        ParamsWrongException(String msg) {
            super(msg);
        }
    }
}
