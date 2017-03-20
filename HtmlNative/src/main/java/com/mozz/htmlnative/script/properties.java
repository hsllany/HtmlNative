package com.mozz.htmlnative.script;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mozz.htmlnative.RVSandBoxContext;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * @author Yang Tao, 17/2/28.
 */


public final class properties {

    private static final String TAG = properties.class.getSimpleName();

    public static class property extends OneArgFunction {

        private RVSandBoxContext RVSandBoxContext;

        public property(RVSandBoxContext context) {
            RVSandBoxContext = context;
        }

        @Override
        public LuaValue call(@NonNull LuaValue luaValue) {
            if (luaValue.istable()) {
                LuaTable table = (LuaTable) luaValue;

                LuaValue k = LuaValue.NIL;
                while (true) {
                    Varargs n = table.next(k);
                    if ((k = n.arg1()).isnil()) {
                        break;
                    }
                    LuaValue v = n.arg(2);
                    Log.d(TAG, k.tojstring() + v.tojstring() + "");
                    RVSandBoxContext.addVariable(k.toString(), toObject(v));
                }
                return LuaValue.TRUE;
            } else {
                return LuaValue.FALSE;
            }
        }


    }

    public static class setProperty extends TwoArgFunction {

        private RVSandBoxContext RVSandBoxContext;

        public setProperty(RVSandBoxContext RVSandBoxContext) {
            this.RVSandBoxContext = RVSandBoxContext;
        }

        @Override
        public LuaValue call(@NonNull LuaValue luaValue, @NonNull LuaValue luaValue1) {
            if (luaValue.isstring()) {
                RVSandBoxContext.updateVariable(luaValue.tojstring(), toObject(luaValue1));
                return LuaValue.TRUE;
            }
            return LuaValue.FALSE;
        }
    }

    public static class getProperty extends OneArgFunction {

        private RVSandBoxContext RVSandBoxContext;

        public getProperty(RVSandBoxContext context) {
            this.RVSandBoxContext = context;
        }

        @Nullable
        @Override
        public LuaValue call(@NonNull LuaValue luaValue) {
            if (luaValue.isstring()) {
                String key = luaValue.tojstring();

                Object val = RVSandBoxContext.getVariable(key);
                if (val != null) {
                    return toLuaValue(val);
                }
                return LuaValue.NIL;
            }

            return LuaValue.NIL;
        }
    }

    private static Object toObject(@NonNull LuaValue value) {
        if (value.isstring()) {
            return value.tojstring();
        } else if (value.isboolean()) {
            return value.toboolean();
        } else if (value.isint()) {
            return value.toint();
        } else if (value.isnumber()) {
            return value.todouble();
        } else {
            return null;
        }
    }

    @Nullable
    private static LuaValue toLuaValue(Object value) {
        if (value instanceof String) {
            return LuaValue.valueOf((String) value);
        } else if (value instanceof Boolean) {
            return LuaValue.valueOf((Boolean) value);
        } else if (value instanceof Integer) {
            return LuaValue.valueOf((Integer) value);
        } else if (value instanceof Double) {
            return LuaValue.valueOf((Double) value);
        } else {
            return null;
        }
    }

}
