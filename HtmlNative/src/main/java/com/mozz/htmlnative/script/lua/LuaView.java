package com.mozz.htmlnative.script.lua;

import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.AttrApplyException;
import com.mozz.htmlnative.AttrsSet;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.attrs.Attr;
import com.mozz.htmlnative.attrs.LayoutAttr;
import com.mozz.htmlnative.common.MainHandler;
import com.mozz.htmlnative.common.Utils;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/23.
 */

public class LuaView extends LuaTable {

    public LuaView(final View v, final HNSandBoxContext context) {
        set("toString", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaView.valueOf(v.toString());
            }
        });

        set("style", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String style = arg.tojstring();

                final Map<String, String> params = Utils.parseStyle(style);

                final Attr viewAttr = AttrsSet.getViewAttr(v);
                final Attr extraAttr = AttrsSet.getExtraAttr(v);
                final LayoutAttr parentAttr = AttrsSet.getParentAttr(v.getParent());
                MainHandler.instance().post(new Runnable() {
                    @Override
                    public void run() {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            ViewGroup parent = null;
                            if (v.getParent() instanceof ViewGroup) {
                                parent = (ViewGroup) v.getParent();
                            }
                            try {
                                AttrsSet.applyAttrToView(v.getContext(), null, context, v, entry
                                        .getKey(), entry.getValue(), null, parent, null, null,
                                        viewAttr, extraAttr, parentAttr, null);
                            } catch (AttrApplyException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                return NIL;
            }

        });

    }


    @Override
    public int type() {
        return TUSERDATA;
    }

    @Override
    public String typename() {
        return TYPE_NAMES[3];
    }

}
