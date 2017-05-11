package com.mozz.htmlnative.script.lua;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mozz.htmlnative.utils.MainHandlerUtils;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/27.
 */
public class LToast extends OneArgFunction implements LApi {
    private WeakReference<Context> mContext;

    public LToast(Context context) {
        mContext = new WeakReference<Context>(context);
    }

    @Override
    public LuaValue call(@NonNull final LuaValue luaValue) {
        MainHandlerUtils.instance().post(new Runnable() {
            @Override
            public void run() {
                Context context = mContext.get();
                if (context != null) {
                    String msg = luaValue.tojstring();
                    android.widget.Toast.makeText(context, msg, android.widget.Toast
                            .LENGTH_SHORT).show();
                }
            }
        });


        return LuaValue.NIL;
    }

    @Override
    public String apiName() {
        return "toast";
    }
}
