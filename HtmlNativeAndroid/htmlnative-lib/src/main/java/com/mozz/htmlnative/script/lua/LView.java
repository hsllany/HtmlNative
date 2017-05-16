package com.mozz.htmlnative.script.lua;

import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.InheritStyleStack;
import com.mozz.htmlnative.dom.AttachedElement;
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
    volatile boolean mAdded;
    private volatile boolean mCreated;
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

                    MainHandlerUtils.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            LayoutParamsLazyCreator tempCreator = new LayoutParamsLazyCreator();
                            ViewGroup parent = (mView.getParent() != null && mView.getParent()
                                    instanceof ViewGroup) ? (ViewGroup) mView.getParent() : null;
                            try {
                                HNRenderer.renderStyle(mView.getContext(), mContext, mView,
                                        mDomElement, tempCreator, parent, styleMaps, false, null);
                                LayoutParamsLazyCreator.createLayoutParams(tempCreator, mView
                                        .getLayoutParams());
                                mView.requestLayout();

                            } catch (AttrApplyException e) {
                                e.printStackTrace();
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

        set("getClass", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                String[] classes = ((AttachedElement) mView.getTag()).getClazz();
                LuaTable classesLua = new LuaTable();
                if (classes != null && classes.length > 0) {
                    for (String clazz : classes) {
                        if (clazz != null) {
                            classesLua.add(LuaValue.valueOf(clazz));
                        }
                    }

                    return classesLua;
                }
                return LuaTable.NIL;
            }
        });

        set("appendChild", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (mCreated && arg instanceof LView && mView instanceof ViewGroup) {
                    final LView child = (LView) arg;
                    if (!child.mCreated) {
                        if (!child.mAdded) {
                            MainHandlerUtils.instance().post(new Runnable() {
                                @Override
                                public void run() {
                                    LayoutParamsLazyCreator creator = new LayoutParamsLazyCreator();
                                    try {
                                        // This must be invoked before HNRenderer.createView
                                        child.mDomElement.setParent(mDomElement);

                                        // Compute the inherit style of parent
                                        InheritStyleStack inheritStyleStack = HNRenderer
                                                .computeInheritStyle(mView);

                                        child.mView = HNRenderer.createView(null, child
                                                .mDomElement, child.mContext, (ViewGroup) mView,
                                                mView.getContext(), null, creator, child.mContext
                                                        .getSegment().getStyleSheet(),
                                                inheritStyleStack);

                                        try {
                                            HNRenderer.renderStyle(child.mView.getContext(),
                                                    mContext, child.mView, child.mDomElement,
                                                    creator, (ViewGroup) mView, child
                                                            .mInlineStyleRaw, false,
                                                    inheritStyleStack);
                                        } catch (AttrApplyException e) {
                                            e.printStackTrace();
                                        }

                                        child.mCreated = true;
                                        // consume the inline style
                                        child.mInlineStyleRaw = null;
                                        ((ViewGroup) mView).addView(child.mView,
                                                LayoutParamsLazyCreator.createLayoutParams(mView,
                                                        creator));
                                        child.mAdded = true;

                                    } catch (HNRenderer.HNRenderException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
                return LuaValue.NIL;
            }
        });

        set("removeChild", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (mAdded && arg instanceof LView && mView instanceof ViewGroup) {
                    final LView toRemoved = (LView) arg;

                    MainHandlerUtils.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (toRemoved.mAdded) {
                                ((ViewGroup) mView).removeView(toRemoved.mView);
                            }
                        }
                    });

                }
                return LuaValue.NIL;
            }
        });

        set("children", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (mView instanceof ViewGroup && mAdded) {
                    LuaTable children = new LuaTable();

                    int childCount = ((ViewGroup) mView).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        LView lView = new LView(((ViewGroup) mView).getChildAt(i), mContext);
                        children.add(lView);
                    }

                    return children;
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
