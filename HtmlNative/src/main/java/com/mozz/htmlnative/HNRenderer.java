package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

import com.mozz.htmlnative.common.Performance;
import com.mozz.htmlnative.common.PerformanceWatcher;
import com.mozz.htmlnative.css.CssSelector;
import com.mozz.htmlnative.view.HNViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Render views
 */
public final class HNRenderer {

    private static final String TAG = HNRenderer.class.getSimpleName();

    /**
     * cache the constructor for later use
     */
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new
            HashMap<>();

    private final Object[] mConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class};

    private static WebViewCreator sWebViewHandler = DefaultWebViewCreator.sInstance;
    private static ImageViewAdapter sImageViewAdapter = DefaultImageAdapter.sInstance;
    private static HrefLinkHandler sHrefLinkHandler = DefaultHrefLinkHandler.sInstance;

    static class ViewCreateResult {
        String id;
        String clazz;
    }

    private static class SelectorMapHolder {
        Set<CssSelector> instance;
    }

    private ViewCreateResult mTempResult;
    private SelectorMapHolder mMapHolder;

    private HNRenderer() {
        mTempResult = new ViewCreateResult();
        mMapHolder = new SelectorMapHolder();
    }

    @NonNull
    public static HNRenderer get() {
        return new HNRenderer();
    }

    @MainThread
    final View render(@NonNull Context context, @NonNull HNSegment segment, @NonNull ViewGroup
            .LayoutParams params) throws RemoteInflateException {
        HNEventLog.writeEvent(HNEventLog.TAG_RENDER, "start to render " + segment.toString());
        HNViewGroup rootViewGroup = new HNViewGroup(context);

        HNSandBoxContext sandBoxContext = SandBoxContextImpl.create(rootViewGroup, segment,
                context);

        this.performCreate(sandBoxContext);

        View v = renderInternal(context, sandBoxContext, segment.mRootTree, segment,
                rootViewGroup, params, rootViewGroup, segment.mCss, mMapHolder.instance,
                mMapHolder);

        if (v != null) {
            rootViewGroup.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            this.performCreated(sandBoxContext);

            HNEventLog.writeEvent(HNEventLog.TAG_RENDER, sandBoxContext.allIdTag());

            return rootViewGroup;
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

    private View renderInternal(@NonNull Context context, @NonNull HNSandBoxContext
            sandBoxContext, HNDomTree tree, HNSegment segment, @NonNull ViewGroup parent,
                                @NonNull ViewGroup.LayoutParams params, @NonNull HNViewGroup
                                        root, Css css, Set<CssSelector> parentSelector,
                                SelectorMapHolder holder) throws RemoteInflateException {

        AttrsSet attrsSet = segment.mAttrs;

        if (tree.isLeaf()) {
            return createViewFromNodeName(tree, sandBoxContext, parent, context, attrsSet,
                    params, root, css, parentSelector, holder);
        } else {
            View view = createViewFromNodeName(tree, sandBoxContext, parent, context, attrsSet,
                    params, root, css, parentSelector, holder);

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

                    // Recursively render child.
                    final View v = renderInternal(context, sandBoxContext, child, segment,
                            viewGroup, layoutParams, root, css, holder.instance, holder);

                    if (v != null) {
                        viewGroup.addView(v, layoutParams);
                    } else {
                        HNEventLog.writeError(HNEventLog.TAG_RENDER, "error when inflating " +
                                child.getTag());
                    }
                }
            } else {
                HNEventLog.writeError(HNEventLog.TAG_RENDER, "View render from HNRenderer is not " +
                        "an " +
                        "viewGroup" +
                        view.getClass().getSimpleName() +
                        ", but related HNDomTree has children. Will ignore its children!");
            }

            return view;
        }
    }


    private View createViewFromNodeName(@NonNull HNDomTree tree, @NonNull HNSandBoxContext
            sandBoxContext, @NonNull ViewGroup parent, @NonNull Context context, @NonNull
            AttrsSet attrsSet, @NonNull ViewGroup.LayoutParams params, @NonNull HNViewGroup root,
                                        Css css, Set<CssSelector> parentSelector,
                                        SelectorMapHolder outSelectors) throws
            RemoteInflateException {

        String tag = tree.getTag();
        PerformanceWatcher watcher = Performance.newWatcher();

        try {

            View v;
            if (HtmlTag.isDivOrTemplate(tag)) {
                v = attrsSet.createViewByTag(this, context, tag, tree);
            } else {
                v = createViewByTag(context, tag);
            }

            if (v == null) {
                Log.e(TAG, "createViewFromNodeName createViewByTag: view is null with tag " + tag);
                return null;
            }

            watcher.check("create view" + v.toString());

            if (v instanceof WebView) {
                root.addWebView((WebView) v);
            }

            try {
                attrsSet.apply(context, sandBoxContext, v, tree, tree.getInner(), tree.getTag(),
                        parent, params, mTempResult);
            } catch (AttrApplyException e) {
                e.printStackTrace();
            }

            Set<CssSelector> cssSelectors = css.matchedSelector(tag, mTempResult.id, mTempResult
                    .clazz, parentSelector);

            outSelectors.instance = cssSelectors;

            try {
                for (CssSelector selector : cssSelectors) {
                    if (selector.next() == null) {
                        css.mCssSet.apply(context, sandBoxContext, v, selector, tree.getInner(),
                                tree.getTag(), parent, params, mTempResult);
                    }

                }
            } catch (AttrApplyException e) {
                e.printStackTrace();
            }

            watcher.checkDone("create view " + v.toString() + ", and give it attrs.");
            return v;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class not found " + tag);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor is missing " + tag);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be accessed " + tag);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be invoked " + tag);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's method has something wrong " + tag);
        }

    }

    @Nullable
    final View createViewByTag(@NonNull Context context, @Nullable String tagName) throws
            ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        String viewClassName = ViewTagLookupTable.findClassByTag(tagName);
        if (viewClassName == null) {
            return null;
        }

        HNEventLog.writeEvent(HNEventLog.TAG_ATTR, "create view" + viewClassName + " with tag" +
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
