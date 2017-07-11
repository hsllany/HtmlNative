package com.mozz.htmlnative.script.lua;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.HtmlTag;
import com.mozz.htmlnative.InheritStyleStack;
import com.mozz.htmlnative.css.Styles;
import com.mozz.htmlnative.css.stylehandler.LayoutStyleHandler;
import com.mozz.htmlnative.css.stylehandler.StyleHandler;
import com.mozz.htmlnative.css.stylehandler.StyleHandlerFactory;
import com.mozz.htmlnative.dom.AttachedElement;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.parser.CssParser;
import com.mozz.htmlnative.utils.MainHandlerUtils;
import com.mozz.htmlnative.view.LayoutParamsCreator;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.mozz.htmlnative.view.LayoutParamsCreator.createLayoutParams;

/**
 * @author Yang Tao, 17/3/23.
 */

class LView extends LObject {

    private static final int INSERT_LAST = -1;
    private static final int INSERT_FIRST = 0;

    private View mView;
    volatile boolean mAdded;
    private volatile boolean mCreated;
    private HNSandBoxContext mContext;
    private DomElement mDomElement;

    // for cache insert information
    private Map<String, Object> mInlineStyleRaw;
    private String mToBeAddText;
    private List<LView> mToBeAdded;
    private int mInsertIndex = -1;

    private static StringBuilder sParserBuffer = new StringBuilder();
    private final Object mLock = new Object();

    LView(final DomElement domElement, Map<String, Object> inlineStyle, final HNSandBoxContext
            context) {
        super();
        mDomElement = domElement;
        mInlineStyleRaw = inlineStyle;
        mContext = context;
        mCreated = false;
        mAdded = false;

        initLuaFunction();
    }

    /**
     * Used only by {@link LDocument.LFindViewById}, which will look up view in existing view tree.
     */
    LView(final View v, final HNSandBoxContext context) {
        this((DomElement) v.getTag(), null, context);
        mView = v;
        mCreated = true;
        mAdded = true;
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

        set("setText", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                final String s = arg.tojstring();
                if (mCreated) {
                    if (mView instanceof TextView) {
                        MainHandlerUtils.instance().post(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) mView).setText(s);
                            }
                        });
                    }
                } else {
                    mToBeAddText = arg.tojstring();
                }
                return LuaValue.NIL;
            }
        });

        set("setAttribute", new OneArgFunction() {
            @Override
            public LuaValue call(final LuaValue arg) {
                if (mCreated) {
                    String style = arg.tojstring();
                    final Map<String, Object> styleMaps = new HashMap<>();
                    CssParser.parseInlineStyle(style, sParserBuffer, styleMaps);

                    MainHandlerUtils.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            LayoutParamsCreator tempCreator = new LayoutParamsCreator(mView
                                    .getLayoutParams());
                            ViewGroup parent = (mView.getParent() != null && mView.getParent()
                                    instanceof ViewGroup) ? (ViewGroup) mView.getParent() : null;
                            try {
                                HNRenderer.renderStyle(mView.getContext(), mContext, mView,
                                        mDomElement, tempCreator, parent, styleMaps, null);
                                createLayoutParams(tempCreator, mView.getLayoutParams());
                                mView.requestLayout();

                            } catch (AttrApplyException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    final Map<String, Object> newStyle = new HashMap<>();
                    CssParser.parseInlineStyle(arg.tojstring(), sParserBuffer, newStyle);

                    MainHandlerUtils.instance().post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (mLock) {
                                if (mInlineStyleRaw != null) {
                                    mInlineStyleRaw.putAll(newStyle);
                                }
                            }
                        }
                    });
                }
                return NIL;
            }
        });

        set("id", new ZeroArgFunction() {
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
                }

        );

        set("className", new ZeroArgFunction() {
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
                }

        );

        set("appendChild", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg instanceof LView) {
                    final LView child = (LView) arg;
                    appendTo(LView.this, child);
                }
                return LuaValue.NIL;
            }
        });

        set("insertBefore", new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg) {
                        if (arg instanceof LView) {
                            final LView child = (LView) arg;
                            child.mInsertIndex = INSERT_FIRST;
                            appendTo(LView.this, child);
                        }
                        return LuaValue.NIL;
                    }
                }

        );

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
                                toRemoved.mAdded = false;
                            }
                        }
                    });

                }
                return LuaValue.NIL;
            }
        });

        set("childNodes", new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if (mView instanceof ViewGroup && mAdded) {
                            LuaTable children = new LuaTable();

                            int childCount = ((ViewGroup) mView).getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                LView lView = new LView(((ViewGroup) mView).getChildAt(i),
                                        mContext);
                                children.add(lView);
                            }
                            return children;
                        }
                        return LuaValue.NIL;
                    }
                }

        );

        set("getAttribute", new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg) {
                        if (mAdded && mCreated) {
                            StyleHandler styleHandler = StyleHandlerFactory.get(mView);
                            StyleHandler extraHandler = StyleHandlerFactory.extraGet(mView);
                            LayoutStyleHandler parentHandler = StyleHandlerFactory.parentGet(mView);
                            Object object = Styles.getStyle(mView, arg.tojstring(), styleHandler,
                                    extraHandler, parentHandler);

                            if (object != null) {
                                return LuaString.valueOf(object.toString());
                            } else {
                                return LuaValue.NIL;
                            }
                        } else {
                            return LuaValue.NIL;
                        }
                    }
                }

        );

        set("tagName", new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if (mCreated) {
                            AttachedElement attachedElement = (AttachedElement) mView.getTag();
                            return LuaString.valueOf(attachedElement.getType());
                        } else {
                            return LuaString.valueOf("");
                        }
                    }
                }

        );

        set("parentNode", new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if (mCreated && mAdded) {
                            ViewParent parent = mView.getParent();
                            if (parent instanceof ViewGroup) {
                                return new LView((View) parent, mContext);
                            }
                        }
                        return LuaValue.NIL;
                    }
                }

        );


        set("hasChildNode", new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if (mCreated && mAdded) {
                            if (mView instanceof ViewGroup) {
                                boolean hasChild = ((ViewGroup) mView).getChildCount() > 0;
                                return LuaBoolean.valueOf(hasChild);
                            }
                        }

                        return LuaBoolean.FALSE;
                    }
                }

        );
    }

    private static void appendTo(LView parent, LView child) {
        if (!HtmlTag.isGroupingElement(parent.mDomElement.getType())) {
            return;
        }

        if (parent.mAdded) {
            generateAndroidViewAndAppend(parent, child);
        } else {
            synchronized (parent.mLock) {
                if (parent.mToBeAdded == null) {
                    parent.mToBeAdded = new LinkedList<>();
                }

                parent.mToBeAdded.add(child);
            }
        }
    }

    private static void generateAndroidViewAndAppend(final LView parent, final LView child) {
        if (!parent.mAdded) {
            return;
        }
        MainHandlerUtils.instance().post(new Runnable() {
            @Override
            public void run() {
                if (child.mAdded) {
                    return;
                }

                LayoutParamsCreator creator = new LayoutParamsCreator();
                try {

                    if (!child.mCreated) {
                        // This must be invoked before HNRenderer.createView
                        child.mDomElement.setParent(parent.mDomElement);

                        // Compute the inherit style of parent
                        InheritStyleStack inheritStyleStack = HNRenderer.computeInheritStyle
                                (parent.mView);

                        child.mView = HNRenderer.createView(null, child.mDomElement, child
                                .mContext, (ViewGroup) parent.mView, parent.mView.getContext(),
                                null, creator, child.mContext.getSegment().getStyleSheet(),
                                inheritStyleStack);

                        Map<String, Object> inlineStyles;
                        synchronized (child.mLock) {
                            inlineStyles = child.mInlineStyleRaw;
                        }

                        try {
                            HNRenderer.renderStyle(child.mView.getContext(), parent.mContext,
                                    child.mView, child.mDomElement, creator, (ViewGroup) parent
                                            .mView, inlineStyles, inheritStyleStack);

                            if (child.mToBeAddText != null) {
                                if (child.mView instanceof TextView) {
                                    ((TextView) child.mView).setText(child.mToBeAddText);
                                }

                                child.mToBeAddText = null;
                            }
                        } catch (AttrApplyException e) {
                            e.printStackTrace();
                        }

                        child.mCreated = true;
                    }
                    if (child.mInsertIndex == INSERT_LAST) {
                        HNRenderer.addView((ViewGroup) parent.mView, child.mView, creator);
                    } else {
                        HNRenderer.addView((ViewGroup) parent.mView, child.mView, creator, child
                                .mInsertIndex);
                    }

                    child.mAdded = true;

                    if (child.mToBeAdded != null) {
                        for (LView grandChild : child.mToBeAdded) {
                            generateAndroidViewAndAppend(child, grandChild);
                        }
                    }

                    synchronized (child.mLock) {
                        // consume the inline style
                        child.mInlineStyleRaw = null;
                        child.mToBeAdded = null;
                    }
                } catch (HNRenderer.HNRenderException e) {
                    e.printStackTrace();
                }
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
    String onObjectClassName() {
        return mDomElement.getType();
    }
}