package com.mozz.htmlnative;

import android.content.Context;
import android.os.SystemClock;
import android.os.Trace;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.htmlnative.css.AttrsSet;
import com.mozz.htmlnative.css.InheritStylesRegistry;
import com.mozz.htmlnative.css.StyleSheet;
import com.mozz.htmlnative.css.Styles;
import com.mozz.htmlnative.css.selector.CssSelector;
import com.mozz.htmlnative.css.stylehandler.LayoutStyleHandler;
import com.mozz.htmlnative.css.stylehandler.StyleHandler;
import com.mozz.htmlnative.css.stylehandler.StyleHandlerFactory;
import com.mozz.htmlnative.dom.AttachedElement;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.dom.HNDomTree;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.HNRootView;
import com.mozz.htmlnative.view.LayoutParamsCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mozz.htmlnative.HNEnvironment.PERFORMANCE_TAG;
import static com.mozz.htmlnative.ViewTypeRelations.BOX;
import static com.mozz.htmlnative.ViewTypeRelations.FLEX_BOX;
import static com.mozz.htmlnative.ViewTypeRelations.LINEAR_BOX;
import static com.mozz.htmlnative.css.Styles.ATTR_DISPLAY;

/**
 * Render views
 */
public final class HNRenderer {

    /**
     * cache the constructor for later use
     */
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new
            HashMap<>();

    private static final Map<String, ViewFactory> sViewFactory = new HashMap<>();

    private static final Object[] sConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class};

    private InheritStyleStack mInheritStyleStack;

    private Tracker mTracker;

    private HNRenderer() {
        mInheritStyleStack = new InheritStyleStack();
        mTracker = new Tracker();
    }

    @NonNull
    public static HNRenderer get() {
        return new HNRenderer();
    }

    public static InheritStyleStack computeInheritStyle(View view) {

        StyleHandler viewStyleHandler = StyleHandlerFactory.get(view);
        StyleHandler extraStyleHandler = StyleHandlerFactory.extraGet(view);
        LayoutStyleHandler parentLayoutAttr = StyleHandlerFactory.parentGet(view);

        InheritStyleStack inheritStyleStack = new InheritStyleStack();
        inheritStyleStack.push();

        Iterator<String> itr = InheritStylesRegistry.iterator();

        while (itr.hasNext()) {
            String params = itr.next();

            Object val = Styles.getStyle(view, params, viewStyleHandler, extraStyleHandler,
                    parentLayoutAttr);
            if (val != null) {
                inheritStyleStack.newStyle(params, val);
            }
        }

        return inheritStyleStack;
    }

    @MainThread
    final View render(@NonNull Context context, @NonNull HNSegment segment) throws
            HNRenderException {

        Trace.beginSection("NHRenderer render start");

        mTracker.reset();

        HNLog.d(HNLog.RENDER, "start to render " + segment.toString());
        HNRootView rootViewGroup = new HNRootView(context);

        HNSandBoxContext sandBoxContext = HNSandBoxContextImpl.createContext(rootViewGroup,
                segment, context);


        mInheritStyleStack.reset();

        LayoutParamsCreator rootCreator = new LayoutParamsCreator();

        long renderStartTime = SystemClock.currentThreadTimeMillis();
        View v = renderInternal(context, sandBoxContext, segment.getDom(), segment,
                rootViewGroup, rootCreator, rootViewGroup, segment.getStyleSheet());


        if (v != null) {
            rootViewGroup.addContent(v, LayoutParamsCreator.createLayoutParams(rootViewGroup,
                    rootCreator));
            mTracker.record("Render View", SystemClock.currentThreadTimeMillis() - renderStartTime);

            long createTime = SystemClock.currentThreadTimeMillis();
            this.performCreate(sandBoxContext);
            mTracker.record("Create View", SystemClock.currentThreadTimeMillis() - createTime);

            long afterCreate = SystemClock.currentThreadTimeMillis();
            this.performCreated(sandBoxContext);
            mTracker.record("After View Created", SystemClock.currentThreadTimeMillis() -
                    afterCreate);

            Log.i(PERFORMANCE_TAG, mTracker.dump());

            HNLog.d(HNLog.RENDER, sandBoxContext.allIdTag());
            Trace.endSection();
            return rootViewGroup;
        }

        Trace.endSection();
        return null;
    }

    private View renderInternal(@NonNull Context context, @NonNull HNSandBoxContext
            sandBoxContext, HNDomTree dom, HNSegment segment, @NonNull ViewGroup parent, @NonNull LayoutParamsCreator paramsCreator, @NonNull HNRootView root, StyleSheet
            styleSheet) throws HNRenderException {

        AttrsSet attrsSet = segment.getInlineStyles();

        if (dom.isLeaf()) {
            View v = createView(dom, dom, sandBoxContext, parent, context, attrsSet,
                    paramsCreator, styleSheet, mInheritStyleStack);
            mInheritStyleStack.pop();
            return v;
        } else {
            View view = createView(dom, dom, sandBoxContext, parent, context, attrsSet,
                    paramsCreator, styleSheet, mInheritStyleStack);

            if (view == null) {
                return null;
            }


            if (view instanceof ViewGroup) {

                final ViewGroup viewGroup = (ViewGroup) view;

                List<HNDomTree> children = dom.children();
                for (HNDomTree child : children) {

                    LayoutParamsCreator childCreator = new LayoutParamsCreator();

                    // Recursively render child.
                    final View v = renderInternal(context, sandBoxContext, child, segment,
                            viewGroup, childCreator, root, styleSheet);

                    if (v != null) {
                        addView(viewGroup, v, childCreator);
                    } else {
                        HNLog.e(HNLog.RENDER, "error when inflating " + child.getType());
                    }
                }
            } else {
                HNLog.e(HNLog.RENDER, "View render from HNRenderer is not " + "an " + "viewGroup"
                        + view.getClass().getSimpleName() + ", but related HNDomTree has " +
                        "children" + ". Will ignore its children!");
            }

            mInheritStyleStack.pop();
            return view;
        }
    }


    public static View createView(AttrsSet.AttrsOwner owner, @NonNull DomElement element,
                                  @NonNull HNSandBoxContext sandBoxContext, ViewGroup parent,
                                  @NonNull Context context, AttrsSet attrsSet, @NonNull LayoutParamsCreator layoutCreator, StyleSheet
                                          styleSheet, InheritStyleStack stack) throws
            HNRenderException {

        String type = element.getType();

        if (stack != null) {
            stack.push();
        }

        try {
            View v;
            if (HtmlTag.isGroupingElement(type)) {
                v = createAndroidViewGroup(context, type, owner, attrsSet, layoutCreator);
            } else {
                v = createAndroidView(context, type);
            }

            if (v == null) {
                HNLog.e(HNLog.RENDER, "createView createDiv: view is null with tag " + type);
                return null;
            }

            //attach the dom element to view
            DomElement domElement = AttachedElement.cloneIfNecessary(element);
            domElement.setParent((DomElement) parent.getTag());
            v.setTag(AttachedElement.cloneIfNecessary(element));


            // save the id if element has one
            String id = element.getId();
            if (id != null) {
                sandBoxContext.registerId(id, v);
            }

            // ------- below starts the styleSheet process part -------

            // 1 - find the related StyleHandler

            StyleHandler viewStyleHandler = StyleHandlerFactory.get(v);
            StyleHandler extraStyleHandler = StyleHandlerFactory.extraGet(v);
            LayoutStyleHandler parentLayoutAttr = StyleHandlerFactory.parentGet(v);

            // 2 - set initial style to an view
            try {
                Styles.setDefaultStyle(context, sandBoxContext, v, element, parent,
                        viewStyleHandler, extraStyleHandler, parentLayoutAttr, layoutCreator);
            } catch (AttrApplyException e) {
                e.printStackTrace();
            }

            // 3 - use parent inherit style
            try {
                /*
                 * First apply the parent styleSheet style to it.
                 */
                if (stack != null) {
                    for (Styles.StyleEntry entry : stack) {

                        // here pass InheritStyleStack null to Styles, is to prevent Style being
                        // stored in InheritStyleStack twice
                        Styles.applySingleStyle(context, sandBoxContext, v, element,
                                layoutCreator, parent, viewStyleHandler, extraStyleHandler,
                                parentLayoutAttr, entry, null);

                    }
                }
            } catch (AttrApplyException e) {
                e.printStackTrace();
                HNLog.e(HNLog.RENDER, "wrong when apply inherit attr to " + type);
            }

            // 4 - use CSS to render
            if (styleSheet != null) {
                CssSelector[] matchedSelectors = styleSheet.matchedSelector(type, element.getId()
                        , element.getClazz());

                for (CssSelector selector : matchedSelectors) {
                    if (selector != null) {
                        if (selector.matchWhole(element)) {

                            try {
                                Styles.applyStyles(context, sandBoxContext, styleSheet, v,
                                        selector, element, parent, layoutCreator,
                                        viewStyleHandler, extraStyleHandler, parentLayoutAttr,
                                        stack);

                            } catch (AttrApplyException e) {
                                e.printStackTrace();
                                HNLog.e(HNLog.RENDER, "Wrong when apply css style to " + type);
                            }
                        }
                    }
                }
            }

            // 5 - use inline-style to render
            try {
                if (attrsSet != null) {
                    Styles.applyStyles(context, sandBoxContext, attrsSet, v, owner, element,
                            parent, layoutCreator, viewStyleHandler, extraStyleHandler,
                            parentLayoutAttr, stack);
                }
            } catch (AttrApplyException e) {
                e.printStackTrace();
                HNLog.e(HNLog.RENDER, "wrong when apply inline attr to " + type);
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
    static View createAndroidView(@NonNull Context context, @Nullable String typeName) throws
            ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        String viewClassName = ViewTypeRelations.findClassByType(typeName);
        if (viewClassName == null) {
            return null;
        }

        HNLog.d(HNLog.RENDER, "createContext view" + viewClassName + " with type" + typeName);

        // first let viewFactory to hook the create process
        View view = createViewByViewFactory(context, viewClassName);
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

        sConstructorArgs[0] = context;
        view = constructor.newInstance(sConstructorArgs);
        // release the context
        sConstructorArgs[0] = null;
        return view;
    }

    static View createAndroidViewGroup(@NonNull Context context, @Nullable String typeName,
                                       AttrsSet.AttrsOwner owner, AttrsSet attrsSet,
                                       LayoutParamsCreator layoutParamsCreator) throws
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        // set the <body> width to 100%
        layoutParamsCreator.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParamsCreator.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (attrsSet != null) {
            Object displayObj = attrsSet.getStyle(owner, ATTR_DISPLAY);
            if (displayObj != null && displayObj instanceof String) {
                String display = (String) displayObj;
                switch (display) {
                    case Styles.VAL_DISPLAY_FLEX:
                        return createAndroidView(context, FLEX_BOX);
                    case Styles.VAL_DISPLAY_ABSOLUTE:
                        return createAndroidView(context, BOX);

                    case Styles.VAL_DISPLAY_BOX:
                    default:
                        return createAndroidView(context, LINEAR_BOX);
                }
            }
        }

        return createAndroidView(context, LINEAR_BOX);
    }

    public static void renderStyle(Context context, final HNSandBoxContext sandBoxContext, View
            v, DomElement domElement, @NonNull LayoutParamsCreator layoutCreator, @NonNull
            ViewGroup parent, String styleName, Object style, boolean isParent, InheritStyleStack
            stack) throws AttrApplyException {

        StyleHandler viewStyleHandler = StyleHandlerFactory.get(v);
        StyleHandler extraStyleHandler = StyleHandlerFactory.extraGet(v);
        LayoutStyleHandler parentLayoutAttr = StyleHandlerFactory.parentGet(v);

        Styles.applySingleStyle(context, sandBoxContext, v, domElement, layoutCreator, parent,
                viewStyleHandler, extraStyleHandler, parentLayoutAttr, styleName, style, stack);
    }

    public static void renderStyle(Context context, @NonNull final HNSandBoxContext
            sandBoxContext, @NonNull View v, DomElement domElement, @NonNull LayoutParamsCreator layoutCreator, ViewGroup parent, @NonNull Map<String, Object>
            styles, InheritStyleStack stack) throws AttrApplyException {

        final StyleHandler viewStyleHandler = StyleHandlerFactory.get(v);
        final StyleHandler extraStyleHandler = StyleHandlerFactory.extraGet(v);
        final LayoutStyleHandler parentAttr = StyleHandlerFactory.parentGet(v);

        for (Map.Entry<String, Object> entry : styles.entrySet()) {

            try {
                Styles.applySingleStyle(v.getContext(), sandBoxContext, v, domElement,
                        layoutCreator, parent, viewStyleHandler, extraStyleHandler, parentAttr,
                        entry.getKey(), entry.getValue(), stack);


            } catch (AttrApplyException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addView(ViewGroup parent, View v, LayoutParamsCreator creator) {
        if (parent == null || v == null) {
            HNLog.e(HNLog.RENDER, "Wrong when trying to add " + v + " to " + parent + " with " +
                    "creator " + creator);
            return;
        }
        parent.addView(v, LayoutParamsCreator.createLayoutParams(parent, creator));
    }

    public static void addView(ViewGroup parent, View v, LayoutParamsCreator creator, int
            index) {
        if (parent == null || v == null) {
            HNLog.e(HNLog.RENDER, "Wrong when trying to add " + v + " to " + parent + " with " +
                    "creator " + creator);
            return;
        }
        parent.addView(v, index, LayoutParamsCreator.createLayoutParams(parent, creator));
    }

    public static void registerViewFactory(String androidClassName, ViewFactory factory) {
        sViewFactory.put(androidClassName, factory);
    }

    public static void unregisterViewFactory(String androidClassName) {
        sViewFactory.remove(androidClassName);
    }

    public static void clearAllViewFactory() {
        sViewFactory.clear();
    }

    private static View createViewByViewFactory(Context context, @NonNull String viewClassName) {
        ViewFactory factory = sViewFactory.get(viewClassName);
        if (factory != null) {
            return factory.create(context);
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

    /**
     * Used to hook the view creation process.
     *
     * @author Yang Tao, 17/3/8.
     */

    public interface ViewFactory<T extends View> {
        T create(Context context);
    }
}
