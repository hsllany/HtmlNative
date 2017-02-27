package com.mozz.remoteview.parser.code;

import android.content.Context;
import android.widget.Toast;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.lang.ref.WeakReference;

/**
 * Created by Yang Tao on 17/2/27.
 */
public class toast extends OneArgFunction {
    private WeakReference<Context> mContext;

    public toast(Context context) {
        mContext = new WeakReference<Context>(context);
    }

    @Override
    public LuaValue call(LuaValue luaValue) {
        Context context = mContext.get();
        if (context != null) {
            String msg = luaValue.tojstring();
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }

        return LuaValue.NIL;
    }
}
