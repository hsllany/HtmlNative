package com.mozz.htmlnative.script.lua;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.mozz.htmlnative.HNEnvironment;
import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.dom.AttachedElement;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * @author Yang Tao, 17/5/11.
 */

class LDocument extends LuaTable implements ILGlobalObject {

    LDocument(final HNSandBoxContext sandBoxContext) {
        super();
        set("version", LuaString.valueOf(HNEnvironment.v));
        set("jump", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String uri = arg.tojstring();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                sandBoxContext.getAndroidContext().startActivity(i);

                return LuaValue.NIL;
            }
        });

        set("createView", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue tag, LuaValue style) {
                if (tag instanceof LuaString && style instanceof LuaString) {
                    DomElement domElement = new AttachedElement();
                    domElement.setType(tag.tojstring());

                    LayoutParamsLazyCreator creator = new LayoutParamsLazyCreator();
                    try {
                        View v = HNRenderer.createView(null, domElement, sandBoxContext, null,
                                sandBoxContext.getAndroidContext(), null, creator, null, null);
                        return new LView(v, creator, sandBoxContext);
                    } catch (HNRenderer.HNRenderException e) {
                        e.printStackTrace();
                    }
                }
                return LuaValue.NIL;
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

    @Override
    public String objectName() {
        return "document";
    }
}
