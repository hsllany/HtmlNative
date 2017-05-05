package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.htmlnative.attrs.AttrHandler;
import com.mozz.htmlnative.attrs.AttrsHelper;
import com.mozz.htmlnative.attrs.LayoutAttrHandler;
import com.mozz.htmlnative.css.AttrsSet;
import com.mozz.htmlnative.css.StyleSheet;
import com.mozz.htmlnative.css.Styles;
import com.mozz.htmlnative.css.selector.CssSelector;
import com.mozz.htmlnative.dom.HNDomTree;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.HNViewGroup;
import com.mozz.htmlnative.view.HtmlLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Render views
 */
public final class HNRenderer {

    /**
     * cache the constructor for later use
     */
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new
            HashMap<>();

    private final Object[] mConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class};

    private InheritStyleStack mInheritStyleStack;

    /**
     * level in Dom Tree
     */
    private int level = 0;

    private HNRenderer() {
        mInheritStyleStack = new InheritStyleStack();
    }

    @NonNull
    public static HNRenderer get() {
        return new HNRenderer();
    }

    @MainThread
    final View render(@NonNull Context context, @NonNull HNSegment segment, @NonNull ViewGroup
            .LayoutParams params) throws HNRenderException {
        HNLog.d(HNLog.RENDER, "start to render " + segment.toString());
        HNViewGroup rootViewGroup = new HNViewGroup(context);

        HNSandBoxContext sandBoxContext = HNSandBoxContextImpl.createContext(rootViewGroup,
                segment, context);

        this.performCreate(sandBoxContext);

        /**
         * set the default level to -1
         */
        level = -1;

        mInheritStyleStack.reset();

        View v = renderInternal(context, sandBoxContext, segment.mDom, segment,
                rootViewGroup, params, rootViewGroup, segment.mStyleSheet);

        if (v != null) {
            rootViewGroup.addContent(v, params);
            this.performCreated(sandBoxContext);
            HNLog.d(HNLog.RENDER, sandBoxContext.allIdTag());

            return rootViewGroup;
        }

        return null;
    }

    private View renderInternal(@NonNull Context context, @NonNull HNSandBoxContext
            sandBoxContext, HNDomTree tree, HNSegment segment, @NonNull ViewGroup parent,
                                @NonNull ViewGroup.LayoutParams params, @NonNull HNViewGroup
                                        root, StyleSheet styleSheet) throws HNRenderException {

        AttrsSet attrsSet = segment.mInlineStyles;

        if (tree.isLeaf()) {
            View v = createView(tree, sandBoxContext, parent, context, attrsSet, params, root,
                    styleSheet);
            mInheritStyleStack.pop();
            return v;
        } else {
            View view = createView(tree, sandBoxContext, parent, context, attrsSet, params, root,
                    styleSheet);

            if (view == null) {
                return null;
            }


            if (view instanceof ViewGroup) {

                final ViewGroup viewGroup = (ViewGroup) view;

                List<HNDomTree> children = tree.children();
                for (HNDomTree child : children) {

                    ViewGroup.LayoutParams layoutParams = createLayoutParams(viewGroup);

                    // Recursively render child.
                    final View v = renderInternal(context, sandBoxContext, child, segment,
                            viewGroup, layoutParams, root, styleSheet);

                    if (v != null) {
                        viewGroup.addView(v, layoutParams);
                    } else {
                        HNLog.e(HNLog.RENDER, "error when inflating " + child.getType());
                    }
                }
            } else {
                HNLog.e(HNLog.RENDER, "View render from HNRenderer is not " +
                        "an " +
                        "viewGroup" +
                        view.getClass().getSimpleName() +
                        ", but related HNDomTree has children. Will ignore its children!");
            }

            mInheritStyleStack.pop();
            return view;
        }
    }


    private View createView(@NonNull HNDomTree tree, @NonNull HNSandBoxContext sandBoxContext,
                            @NonNull ViewGroup parent, @NonNull Context context, @NonNull
                                    AttrsSet attrsSet, @NonNull ViewGroup.LayoutParams
                                    layoutParams, @NonNull HNViewGroup root, StyleSheet
                                    styleSheet) throws HNRenderException {

        String type = tree.getType();

        level++;

        mInheritStyleStack.push();

        try {
            View v;
            if (HtmlTag.isDivOrTemplate(type)) {
                Object displayObj = attrsSet.getStyle(tree, "display");
                if (displayObj != null && displayObj instanceof String) {
                    String display = (String) displayObj;
                    switch (display) {
                        case Styles.VAL_DISPLAY_FLEX:
                            v = createViewByTag(context, "flexbox");
                            break;
                        case Styles.VAL_DISPLAY_ABSOLUTE:
                            v = createViewByTag(context, "box");
                            break;

                        case Styles.VAL_DISPLAY_BOX:
                        default:
                            v = createViewByTag(context, "linearbox");
                            break;
                    }
                } else {
                    v = createViewByTag(context, "linearbox");
                }

                // set the <body> width to 100%
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                v = createViewByTag(context, type);
            }

            if (v == null) {
                HNLog.e(HNLog.RENDER, "createView createDiv: view is null with tag " + type);
                return null;
            }

            // ------- below starts the styleSheet process part -------

            // first find the related AttrHandler

            AttrHandler viewAttrHandler = AttrsHelper.getAttrHandler(v);
            AttrHandler extraAttrHandler = AttrsHelper.getExtraAttrHandler(v);
            AttrHandler parentAttrHandler = AttrsHelper.getAttrHandler(v);

            LayoutAttrHandler parentLayoutAttr = null;
            if (parentAttrHandler instanceof LayoutAttrHandler) {
                parentLayoutAttr = (LayoutAttrHandler) parentAttrHandler;
            }

            try {
                /**
                 * First apply the parent styleSheet style to it.
                 */

                for (Styles.StyleEntry entry : mInheritStyleStack) {

                    // here pass InheritStyleStack null to Styles, is to prevent Style being
                    // stored in InheritStyleStack twice
                    Styles.applyStyle(context, sandBoxContext, v, tree, layoutParams, parent,
                            viewAttrHandler, extraAttrHandler, parentLayoutAttr, entry, false,
                            null);
                }


                Styles.apply(context, sandBoxContext, attrsSet, v, tree, tree, parent,
                        layoutParams, true, false, viewAttrHandler, extraAttrHandler,
                        parentLayoutAttr, mInheritStyleStack);

            } catch (AttrApplyException e) {
                e.printStackTrace();
                HNLog.e(HNLog.RENDER, "wrong when apply attr to " + type);
            }

            // core part to handle the styleSheet selectors

            Set<CssSelector> matchedSelectors = styleSheet.matchedSelector(type, tree.getId(),
                    tree.getClazz());

            for (CssSelector selector : matchedSelectors) {
                if (selector.matchWhole(tree)) {

                    try {
                        Styles.apply(context, sandBoxContext, styleSheet, v, selector, tree,
                                parent, layoutParams, false, false, viewAttrHandler,
                                extraAttrHandler, parentLayoutAttr, mInheritStyleStack);
                    } catch (AttrApplyException e) {
                        e.printStackTrace();
                    }
                }
            }
            return v;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new HNRenderException("class not found " + type);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new HNRenderException("class's constructor is missing " + type);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new HNRenderException("class's constructor can not be accessed " + type);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new HNRenderException("class's constructor can not be invoked " + type);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new HNRenderException("class's method has something wrong " + type);
        }

    }

    @Nullable
    final View createViewByTag(@NonNull Context context, @Nullable String tagName) throws
            ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        String viewClassName = ViewRelations.findClassByType(tagName);
        if (viewClassName == null) {
            return null;
        }

        HNLog.d(HNLog.RENDER, "createContext view" + viewClassName + " with tag" +
                tagName);

        // first let viewCreateHandler to createContext view
        View view = createViewByViewHandler(context, viewClassName);
        if (view != null) {
            return view;
        }

        Constructor<? extends View> constructor = sConstructorMap.get(viewClassName);
        Class<? extends View> clazz;
        if (constructor == null) {
            // Class not found in the cache, see if it's real, and try to add it
            clazz = context.getClassLoader().loadClass(viewClassName).asSubclass(View.class);
            constructor = clazz.getConstructor(sConstructorSignature);
            constructor.setAccessible(true);
            sConstructorMap.put(viewClassName, constructor);
        }

        mConstructorArgs[0] = context;
        view = constructor.newInstance(mConstructorArgs);
        return view;
    }

    private View createViewByViewHandler(Context context, @NonNull String viewClassName) {
        if (viewClassName.equals(WebView.class.getName()) && HNative.getWebviewCreator() != null) {
            return HNative.getWebviewCreator().create(context);
        }

        return null;
    }


    private void performCreate(HNSandBoxContext sandBoxContext) {
        if (sandBoxContext != null) {
            sandBoxContext.onViewCreate();
        }
    }

    private void performCreated(HNSandBoxContext sandBoxContext) {
        if (sandBoxContext != null) {
            sandBoxContext.onViewLoaded();
        }
    }

    public static ViewGroup.LayoutParams createLayoutParams(ViewGroup viewGroup) throws
            HNRenderException {
        if (viewGroup instanceof AbsoluteLayout) {
            return new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT, 0, 0);
        } else if (viewGroup instanceof HtmlLayout) {
            return new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        } else if (viewGroup instanceof FlexboxLayout) {
            return new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
        } else {
            throw new HNRenderException("can't create related layoutParams, unknown " +
                    "view type " + viewGroup.toString());
        }
    }


    public static class HNRenderException extends Exception {
        public HNRenderException() {
            super();
        }

        public HNRenderException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public HNRenderException(String detailMessage) {
            super(detailMessage);
        }

        public HNRenderException(Throwable throwable) {
            super(throwable);
        }
    }


}
