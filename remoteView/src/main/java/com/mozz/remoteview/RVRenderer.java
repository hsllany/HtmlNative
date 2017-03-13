package com.mozz.remoteview;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

import com.mozz.remoteview.common.Performance;
import com.mozz.remoteview.common.PerformanceWatcher;
import com.mozz.remoteview.view.RXViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


public final class RVRenderer {

    private static final String TAG = RVRenderer.class.getSimpleName();

    static boolean DEBUG = false;

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<>();

    private final Object[] mConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class};

    private static WebViewCreator sWebViewHandler = DefaultWebViewCreator.sInstance;
    private static ImageViewAdapter sImageViewAdapter = DefaultImageAdapter.sInstance;
    private static HrefLinkHandler sHrefLinkHandler = DefaultHrefLinkHandler.sInstance;

    private RVRenderer() {
    }

    @NonNull
    public static RVRenderer get() {
        return new RVRenderer();
    }

    @MainThread
    final View inflate(@NonNull Context context, @NonNull RVModule rvModule, @NonNull ViewGroup.LayoutParams params)
            throws RemoteInflateException {

        PerformanceWatcher pWatcher = Performance.newWatcher();
        RXViewGroup frameLayout = new RXViewGroup(context);
        RViewContext RViewContext = ViewContextImpl.initViewContext(frameLayout, rvModule, context);
        pWatcher.check("[step 1] create RViewContext");

        RViewContext.onViewCreate();
        pWatcher.check("[step 2] call onViewCreate");

        View v = inflate(context, RViewContext, rvModule.mRootTree, frameLayout, rvModule.mAttrs,
                params, frameLayout);
        pWatcher.check("[step 3] rendering view");

        if (v == null)
            return null;
        frameLayout.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        RViewContext.onViewLoaded();
        pWatcher.checkDone("finally done");

        if (DEBUG) {
            Log.d(TAG, RViewContext.allIdTag());
        }

        return frameLayout;
    }

    private View inflate(@NonNull Context context, @NonNull RViewContext RViewContext, @NonNull RVDomTree tree,
                         @NonNull ViewGroup parent, @NonNull AttrsSet attrsSet, @NonNull ViewGroup.LayoutParams params,
                         @NonNull RXViewGroup root)
            throws RemoteInflateException {


        if (tree.isLeaf()) {
            return createViewFromTag(tree, RViewContext, tree.getNodeName(), parent,
                    context, attrsSet, params, root);
        } else {
            View view = createViewFromTag(tree, RViewContext, tree.getNodeName(), parent,
                    context, attrsSet, params, root);

            if (view == null) {
                return null;
            }


            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;

                for (RVDomTree child : tree.mChildren) {

                    final ViewGroup.LayoutParams layoutParams;
                    if (view instanceof AbsoluteLayout) {
                        layoutParams = new AbsoluteLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0);
                    } else {
                        layoutParams = new ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                    }


                    final View v = inflate(context, RViewContext, child, viewGroup, attrsSet,
                            layoutParams, root);

                    if (v != null) {
                        viewGroup.addView(v, layoutParams);
                    } else {
                        Log.e(TAG, "error when inflating " + child.getNodeName());
                    }
                }
            } else {
                Log.w(TAG, "View inflate from RVRenderer is not an viewGroup" +
                        view.getClass().getSimpleName() +
                        ", but related RVDomTree has children. Will ignore its children!");
            }

            return view;
        }
    }


    private View createViewFromTag(@NonNull RVDomTree tree, @NonNull RViewContext RViewContext, @NonNull String tagName,
                                   @NonNull ViewGroup parent, @NonNull Context context, @NonNull AttrsSet attrsSet,
                                   @NonNull ViewGroup.LayoutParams params, @NonNull RXViewGroup root) throws RemoteInflateException {

        PerformanceWatcher watcher = Performance.newWatcher();
        try {

            if (needFutureCreate(tagName)) {
                View v = attrsSet.createViewViaAttr(this, context, tagName, tree);

                if (v instanceof WebView) {
                    root.addWebView((WebView) v);
                }

                try {
                    attrsSet.apply(context, tagName, RViewContext, v, tree, parent, params);
                } catch (AttrApplyException e) {
                    e.printStackTrace();
                }

                return v;
            } else {
                String viewClazzName = ViewRegistry.findClassByTag(tagName);
                if (viewClazzName == null)
                    return null;
                View view = createView(context, viewClazzName);

                watcher.check("create view" + view.toString());

                if (view instanceof WebView) {
                    root.addWebView((WebView) view);
                }

                try {
                    attrsSet.apply(context, tagName, RViewContext, view, tree, parent, params);
                } catch (AttrApplyException e) {
                    e.printStackTrace();
                }

                watcher.checkDone("create view " + view.toString() + ", and give it attrs.");
                return view;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class not found " + tagName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor is missing " + tagName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be accessed " + tagName);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be invoked " + tagName);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's method has something wrong " + tagName);
        }

    }

    @Nullable
    final View createView(@NonNull Context context, @Nullable String viewClassName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        // first let viewCreateHandler to create view
        View view = createViewByViewHandler(context, viewClassName);
        if (view != null) {
            return view;
        }

        Constructor<? extends View> constructor = sConstructorMap.get(viewClassName);
        Class<? extends View> clazz;
        if (constructor == null) {
            // Class not found in the cache, see if it's real, and try to add it

            if (viewClassName == null)
                throw new ClassNotFoundException("can't find related widget " + viewClassName);

            clazz = context.getClassLoader().loadClass(viewClassName).asSubclass(View.class);

            constructor = clazz.getConstructor(sConstructorSignature);
            constructor.setAccessible(true);
            sConstructorMap.put(viewClassName, constructor);
        }

        mConstructorArgs[0] = context;
        view = constructor.newInstance(mConstructorArgs);
        if (DEBUG) {
            Log.d(TAG, "create view " + view.toString());
        }

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


    private static boolean needFutureCreate(@NonNull String name) {
        if (name.equals(HtmlTag.DIV)) {
            return true;
        } else {
            return false;
        }
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
