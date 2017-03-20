package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

import com.mozz.htmlnative.common.Performance;
import com.mozz.htmlnative.common.PerformanceWatcher;
import com.mozz.htmlnative.view.RXViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;


public final class HNRenderer {

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new
            HashMap<>();

    private final Object[] mConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class};

    private static WebViewCreator sWebViewHandler = DefaultWebViewCreator.sInstance;
    private static ImageViewAdapter sImageViewAdapter = DefaultImageAdapter.sInstance;
    private static HrefLinkHandler sHrefLinkHandler = DefaultHrefLinkHandler.sInstance;

    private HNRenderer() {
    }

    @NonNull
    public static HNRenderer get() {
        return new HNRenderer();
    }

    @MainThread
    final View render(@NonNull Context context, @NonNull HNSegment segment, @NonNull ViewGroup
            .LayoutParams params) throws RemoteInflateException {

        EventLog.writeEvent(EventLog.TAG_RENDER, "start to render " + segment.toString());

        PerformanceWatcher pWatcher = Performance.newWatcher();

        RXViewGroup rootViewGroup = new RXViewGroup(context);

        HNSandBoxContext HNSandBoxContext = SandBoxContextImpl.create(rootViewGroup, segment,
                context);

        pWatcher.check("[step 1] create HNSandBoxContext");

        HNSandBoxContext.onViewCreate();
        pWatcher.check("[step 2] call onViewCreate");

        View v = renderInternal(context, HNSandBoxContext, segment.mRootTree, rootViewGroup,
                segment.mAttrs, params, rootViewGroup);
        pWatcher.check("[step 3] rendering view");

        if (v == null) {
            return null;
        }
        rootViewGroup.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        HNSandBoxContext.onViewLoaded();
        pWatcher.checkDone("finally done");

        EventLog.writeEvent(EventLog.TAG_RENDER, HNSandBoxContext.allIdTag());

        return rootViewGroup;
    }

    private View renderInternal(@NonNull Context context, @NonNull HNSandBoxContext
            sandBoxContext, @NonNull HNDomTree tree, @NonNull ViewGroup parent, @NonNull AttrsSet
            attrsSet, @NonNull ViewGroup.LayoutParams params, @NonNull RXViewGroup root) throws
            RemoteInflateException {


        if (tree.isLeaf()) {
            return createViewFromNodeName(tree, sandBoxContext, tree.getNodeName(), parent,
                    context, attrsSet, params, root);
        } else {
            View view = createViewFromNodeName(tree, sandBoxContext, tree.getNodeName(), parent,
                    context, attrsSet, params, root);

            if (view == null) {
                return null;
            }


            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;

                List<HNDomTree> children = tree.children();
                for (HNDomTree child : children) {

                    final ViewGroup.LayoutParams layoutParams;
                    if (view instanceof AbsoluteLayout) {
                        layoutParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams
                                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0);
                    } else {
                        layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams
                                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }


                    final View v = renderInternal(context, sandBoxContext, child, viewGroup,
                            attrsSet, layoutParams, root);

                    if (v != null) {
                        viewGroup.addView(v, layoutParams);
                    } else {
                        EventLog.writeError(EventLog.TAG_RENDER, "error when inflating " + child
                                .getNodeName());
                    }
                }
            } else {
                EventLog.writeError(EventLog.TAG_RENDER, "View render from HNRenderer is not an " +
                        "viewGroup" +
                        view.getClass().getSimpleName() +
                        ", but related HNDomTree has children. Will ignore its children!");
            }

            return view;
        }
    }


    private View createViewFromNodeName(@NonNull HNDomTree tree, @NonNull HNSandBoxContext
            sandBoxContext, @NonNull String nodeName, @NonNull ViewGroup parent, @NonNull Context
            context, @NonNull AttrsSet attrsSet, @NonNull ViewGroup.LayoutParams params, @NonNull
            RXViewGroup root) throws RemoteInflateException {

        PerformanceWatcher watcher = Performance.newWatcher();
        try {

            if (HtmlTag.isDivOrTemplate(nodeName)) {
                View v = attrsSet.createViewViaAttr(this, context, nodeName, tree);

                if (v instanceof WebView) {
                    root.addWebView((WebView) v);
                }

                try {
                    attrsSet.apply(context, nodeName, sandBoxContext, v, tree, parent, params);
                } catch (AttrApplyException e) {
                    e.printStackTrace();
                }

                return v;
            } else {

                View view = createView(context, nodeName);

                watcher.check("create view" + view.toString());

                if (view instanceof WebView) {
                    root.addWebView((WebView) view);
                }

                try {
                    attrsSet.apply(context, nodeName, sandBoxContext, view, tree, parent, params);
                } catch (AttrApplyException e) {
                    e.printStackTrace();
                }

                watcher.checkDone("create view " + view.toString() + ", and give it attrs.");
                return view;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class not found " + nodeName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor is missing " + nodeName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be accessed " + nodeName);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be invoked " + nodeName);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's method has something wrong " + nodeName);
        }

    }

    @Nullable
    final View createView(@NonNull Context context, @Nullable String tagName) throws
            ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        String viewClassName = ViewTagLookupTable.findClassByTag(tagName);
        if (viewClassName == null) {
            return null;
        }

        EventLog.writeEvent(EventLog.TAG_ATTR, "create view" + viewClassName + " with tag" +
                tagName);

        // first let viewCreateHandler to create view
        View view = createViewByViewHandler(context, viewClassName);
        if (view != null) {
            return view;
        }

        Constructor<? extends View> constructor = sConstructorMap.get(viewClassName);
        Class<? extends View> clazz;
        if (constructor == null) {
            // Class not found in the cache, see if it's real, and try to add it

            if (viewClassName == null) {
                throw new ClassNotFoundException("can't find related widget " + viewClassName);
            }

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
        if (viewClassName.equals(WebView.class.getName()) && sWebViewHandler != null) {
            return sWebViewHandler.create(context);
        }

        return null;
    }

    static void setWebViewCreator(@NonNull WebViewCreator handler) {
        sWebViewHandler = handler;
    }

    static void setImageViewAdapter(@NonNull ImageViewAdapter adapter) {
        sImageViewAdapter = adapter;
    }

    static void setHrefLinkHandler(HrefLinkHandler handler) {
        sHrefLinkHandler = handler;
    }

    public static HrefLinkHandler getHrefLinkHandler() {
        return sHrefLinkHandler;
    }

    @NonNull
    public static ImageViewAdapter getImageViewAdpater() {
        return sImageViewAdapter;
    }


    public static class RemoteInflateException extends Exception {
        public RemoteInflateException() {
            super();
        }

        public RemoteInflateException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public RemoteInflateException(String detailMessage) {
            super(detailMessage);
        }

        public RemoteInflateException(Throwable throwable) {
            super(throwable);
        }
    }


}
