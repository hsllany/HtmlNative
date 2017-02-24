package com.mozz.remoteview.parser;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mozz.remoteview.parser.code.LuaRunner;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.Map;

/**
 * Created by Yang Tao on 17/2/24.
 */

public final class ViewContext {

    private static final String TAG = ViewContext.class.getSimpleName();

    private static final int ViewContextTag = 0x3 << 24;

    Map<String, View> mViewSelector = new ArrayMap<>();

    private Globals mGlobals;

    private RVModule mModule;
    private Context mContext;

    ViewContext(RVModule module, Context context) {
        mModule = module;
        mContext = context;
    }

    @Nullable
    View put(String id, View value) {
        View before = mViewSelector.put(id, value);
        if (before != null) {
            Log.w(TAG, "Duplicated id " + id + ", before is " + before + ", current is " + value);
        }
        return mViewSelector.put(id, value);
    }

    @Nullable
    public View findViewById(@NonNull String id) {
        return mViewSelector.get(id);
    }

    public boolean containsView(String id) {
        return mViewSelector.containsKey(id);
    }

    void onViewLoaded() {

        long time1 = SystemClock.currentThreadTimeMillis();

        mGlobals = LuaRunner.newGlobals();
        mGlobals.set("textColor", new textColor());
        mGlobals.set("toast", new toast());
        mGlobals.set("visible", new visible());

        Log.i(TAG, "init lua module spend " + (SystemClock.currentThreadTimeMillis() - time1) + "ms");
    }

    public static ViewContext getViewContext(FrameLayout v) {
        Object object = v.getTag(ViewContextTag);

        if (object != null && object instanceof ViewContext) {
            return (ViewContext) object;
        }

        return null;
    }

    public void execute(String code) {
        LuaValue l = mGlobals.load(code);
        l.call();
    }

    public static ViewContext initViewContext(FrameLayout layout, RVModule module, Context context) {
        ViewContext v = new ViewContext(module, context);
        layout.setTag(ViewContextTag, v);
        return v;
    }

    private class textColor extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
            try {
                String id = luaValue.tojstring();

                int color = Color.parseColor(luaValue2.tojstring());

                View v = findViewById(id);
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

    private class toast extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue luaValue) {
            String msg = luaValue.tojstring();
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

            return LuaValue.NIL;
        }
    }

    private class visible extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue luaValue, LuaValue luaValue1) {
            String id = luaValue.tojstring();
            boolean visible = luaValue1.toboolean();

            View v = findViewById(id);
            if (v == null) return LuaValue.NIL;
            v.setVisibility(visible ? View.VISIBLE : View.GONE);

            return LuaValue.NIL;
        }
    }
}
