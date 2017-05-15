package com.mozz.htmlnative.script.lua;

import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.attrshandler.AttrHandler;
import com.mozz.htmlnative.attrshandler.AttrsHelper;
import com.mozz.htmlnative.attrshandler.LayoutAttrHandler;
import com.mozz.htmlnative.css.Styles;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.parser.CssParser;
import com.mozz.htmlnative.utils.MainHandlerUtils;
import com.mozz.htmlnative.view.LayoutParamsLazyCreator;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Tao, 17/3/23.
 */

class LView extends LuaTable {

    private View mView;
    boolean mAdded;
    private boolean mCreated;
    private HNSandBoxContext mContext;
    private DomElement mDomElement;
    private Map<String, Object> mInlineStyleRaw;

    private static StringBuilder sParserBuffer = new StringBuilder();

    LView(final DomElement domElement, Map<String, Object> inlineStyle, final HNSandBoxContext
            context) {
        mDomElement = domElement;
        mInlineStyleRaw = inlineStyle;
        mContext = context;
        mCreated = false;
        mAdded = false;

        initLuaFunction();
    }

    LView(final View v, final HNSandBoxContext context) {
        this((DomElement) v.getTag(), null, context);
        mView = v;
        mCreated = true;
    }


    private void initLuaFunction() {
        set("toString", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (mCreated) {
                    return LView.valueOf(mView.toString());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        set("setStyle", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (mCreated) {
                    String style = arg.tojstring();


                    final Map<String, Object> styleMaps = new HashMap<>();
                    CssParser.parseInlineStyle(style, sParserBuffer, styleMaps);

                    final AttrHandler viewAttrHandler = AttrsHelper.getAttrHandler(mView);
                    final AttrHandler extraAttrHandler = AttrsHelper.getExtraAttrHandler(mView);
                    final LayoutAttrHandler parentAttr = AttrsHelper.getParentAttrHandler(mView);
                    MainHandlerUtils.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            for (Map.Entry<String, Object> entry : styleMaps.entrySet()) {
                                ViewGroup parent = null;
                                if (mView.getParent() instanceof ViewGroup) {
                                    parent = (ViewGroup) mView.getParent();
                                }
                                try {
                                    Styles.applyStyle(mView.getContext(), mContext, mView, null,
                                            null, parent, viewAttrHandler, extraAttrHandler,
                                            parentAttr, entry.getKey(), entry.getValue(), false,
                                            null);


                                } catch (AttrApplyException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    });
                } else {
                    Map<String, Object> newStyle = new HashMap<>();
                    CssParser.parseInlineStyle(arg.tojstring(), sParserBuffer, newStyle);
                    mInlineStyleRaw.putAll(newStyle);
                }
                return NIL;
            }

        });

        set("getId", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (mCreated) {
                    Object obj = mView.getTag();
                    if (obj != null && obj instanceof DomElement) {
                        return LuaString.valueOf(((DomElement) obj).getId());
                    }
                }
                return LuaString.valueOf("");
            }
        });

        set("appendChild", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (mCreated) {
                    if (arg instanceof LView) {
                        LView child = (LView) arg;
                        if (mView instanceof ViewGroup && !child.mAdded) {
                            if (!child.mCreated) {
                                LayoutParamsLazyCreator creator = new LayoutParamsLazyCreator();
                                try {
                                    child.mView = HNRenderer.createView(null, child.mDomElement,
                                            child.mContext, (ViewGroup) mView, mView.getContext()
                                            , null, creator, child.mContext.getSegment()
                                                    .getStyleSheet(), null);

                                    final AttrHandler viewAttrHandler = AttrsHelper
                                            .getAttrHandler(child.mView);
                                    final AttrHandler extraAttrHandler = AttrsHelper
                                            .getExtraAttrHandler(child.mView);
                                    final LayoutAttrHandler parentAttr = AttrsHelper
                                            .getParentAttrHandler(child.mView);

                                    for (Map.Entry<String, Object> entry : child.mInlineStyleRaw
                                            .entrySet()) {
                                        ViewGroup parent = null;
                                        if (child.mView.getParent() instanceof ViewGroup) {
                                            parent = (ViewGroup) mView.getParent();
                                        }
                                        try {
                                            Styles.applyStyle(child.mView.getContext(), mContext,
                                                    child.mView, null, null, parent,
                                                    viewAttrHandler, extraAttrHandler,
                                                    parentAttr, entry.getKey(), entry.getValue(),
                                                    false, null);


                                        } catch (AttrApplyException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    child.mCreated = true;
                                    ((ViewGroup) mView).addView(child.mView,
                                            LayoutParamsLazyCreator.createLayoutParams(mView,
                                                    creator));
                                } catch (HNRenderer.HNRenderException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
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

}
