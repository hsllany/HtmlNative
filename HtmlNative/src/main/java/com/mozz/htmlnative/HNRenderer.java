package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.htmlnative.common.Performance;
import com.mozz.htmlnative.common.PerformanceWatcher;
import com.mozz.htmlnative.css.CssSelector;
import com.mozz.htmlnative.view.HNViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
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

    private static WebViewCreator sWebViewHandler = DefaultWebViewCreator.sInstance;
    private static ImageViewAdapter sImageViewAdapter = DefaultImageAdapter.sInstance;
    private static HrefLinkHandler sHrefLinkHandler = DefaultHrefLinkHandler.sInstance;

    private static class SelectorMapHolder {
        Set<CssSelector> instance;
    }

    private CssIdClass mTempResult;
    private SelectorMapHolder mMapHolder;

    private int[] parentCssStack = new int[50];
    private int[] lastCssStackSize = new int[10];
    private int parentIndex = 0;
    private int level = 0;
    private ParentCss mParentCss = new ParentCss();

    private HNRenderer() {
        mTempResult = new CssIdClass();
        mMapHolder = new SelectorMapHolder();

        Arrays.fill(parentCssStack, -1);
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

        HNSandBoxContext sandBoxContext = SandBoxContextImpl.create(rootViewGroup, segment,
                context);

        this.performCreate(sandBoxContext);

        level = -1;

        View v = renderInternal(context, sandBoxContext, segment.mRootTree, segment,
                rootViewGroup, params, rootViewGroup, segment.mCss, mMapHolder.instance,
                mMapHolder);

        if (v != null) {
            rootViewGroup.addView(v, params);
            this.performCreated(sandBoxContext);
            HNLog.d(HNLog.RENDER, sandBoxContext.allIdTag());

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
                                SelectorMapHolder holder) throws HNRenderException {

        AttrsSet attrsSet = segment.mAttrs;

        if (tree.isLeaf()) {
            View v = createViewFromNodeName(tree, sandBoxContext, parent, context, attrsSet,
                    params, root, css, parentSelector, holder);
            parentIndex -= lastCssStackSize[level];
            lastCssStackSize[level] = 0;
            level--;
            debugCssParent("return as single");
            return v;
        } else {
            View view = createViewFromNodeName(tree, sandBoxContext, parent, context, attrsSet,
                    params, root, css, parentSelector, holder);

            if (view == null) {
                return null;
            }


            if (view instanceof ViewGroup) {

                Set<CssSelector> tempSelector = holder.instance;

                final ViewGroup viewGroup = (ViewGroup) view;

                List<HNDomTree> children = tree.children();
                for (HNDomTree child : children) {

                    ViewGroup.LayoutParams layoutParams = null;
                    if (view instanceof AbsoluteLayout) {
                        layoutParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams
                                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0);
                    } else if (view instanceof LinearLayout) {
                        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    } else if (view instanceof FlexboxLayout) {
                        layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams
                                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }

                    // Recursively render child.
                    final View v = renderInternal(context, sandBoxContext, child, segment,
                            viewGroup, layoutParams, root, css, tempSelector, holder);

                    if (v != null) {
                        viewGroup.addView(v, layoutParams);
                    } else {
                        HNLog.e(HNLog.RENDER, "error when inflating " + child.getTag());
                    }
                }
            } else {
                HNLog.e(HNLog.RENDER, "View render from HNRenderer is not " +
                        "an " +
                        "viewGroup" +
                        view.getClass().getSimpleName() +
                        ", but related HNDomTree has children. Will ignore its children!");
            }

            parentIndex -= lastCssStackSize[level];
            lastCssStackSize[level] = 0;
            level--;
            debugCssParent("return as viewgroup");
            return view;
        }
    }


    private View createViewFromNodeName(@NonNull HNDomTree tree, @NonNull HNSandBoxContext
            sandBoxContext, @NonNull ViewGroup parent, @NonNull Context context, @NonNull
            AttrsSet attrsSet, @NonNull ViewGroup.LayoutParams params, @NonNull HNViewGroup root,
                                        Css css, Set<CssSelector> parentSelector,
                                        SelectorMapHolder outSelectors) throws HNRenderException {

        String tag = tree.getTag();
        PerformanceWatcher watcher = Performance.newWatcher();

        level++;

        try {

            View v;

            if (HtmlTag.isDivOrTemplate(tag)) {
                Object displayObj = attrsSet.getAttr(tree, "display");
                if (displayObj != null && displayObj instanceof String) {
                    String display = (String) displayObj;

                    if (display.equals("flex")) {
                        v = createViewByTag(context, "flexbox");
                    } else if (display.equals("absolute")) {
                        v = createViewByTag(context, "box");
                    } else if (display.equals("box")) {
                        v = createViewByTag(context, "linearbox");
                    } else {
                        v = createViewByTag(context, "linearbox");
                    }
                } else {
                    v = createViewByTag(context, "linearbox");
                }
            } else {
                v = createViewByTag(context, tag);
            }

            if (v == null) {
                HNLog.e(HNLog.RENDER, "createViewFromNodeName createDiv: view is null with tag "
                        + tag);
                return null;
            }

            watcher.check("create view" + v.toString());

            if (v instanceof WebView) {
                root.addWebView((WebView) v);
            }

            try {
                debugCssParent("before apply to " + tag);
                // apply parent style first
                for (int i = 0; i < parentIndex; i++) {
                    mParentCss.index = i;

                    if (parentCssStack[i] > 0) {
                        attrsSet.apply(context, sandBoxContext, v, mParentCss, null, tree.getTag
                                (), parent, params, null, false, true);
                    } else {
                        css.mCssSet.apply(context, sandBoxContext, v, mParentCss, null, tree
                                .getTag(), parent, params, null, false, true);
                    }
                }


                attrsSet.apply(context, sandBoxContext, v, tree, tree.getInner(), tree.getTag(),
                        parent, params, mTempResult, true, false);

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
                                tree.getTag(), parent, params, mTempResult, false, false);
                        parentCssStack[parentIndex++] = -selector.attrIndex();
                        lastCssStackSize[level]++;
                    }

                }
            } catch (AttrApplyException e) {
                e.printStackTrace();
            }

            debugCssParent("after apply");

            watcher.checkDone("create view " + v.toString() + ", and give it attrs.");
            return v;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new HNRenderException("class not found " + tag);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new HNRenderException("class's constructor is missing " + tag);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new HNRenderException("class's constructor can not be accessed " + tag);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new HNRenderException("class's constructor can not be invoked " + tag);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new HNRenderException("class's method has something wrong " + tag);
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

        HNLog.d(HNLog.ATTR, "create view" + viewClassName + " with tag" +
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

    private class ParentCss implements AttrsOwner {

        private int index;

        @Override
        public int attrIndex() {
            return parentCssStack[index] > 0 ? parentCssStack[index] : -parentCssStack[index];
        }

        @Override
        public void setAttrIndex(int newIndex) {

        }
    }

    private void debugCssParent(String msg) {
        HNLog.d(HNLog.RENDER, "---------" + msg + "---------");
        HNLog.d(HNLog.RENDER, "parentIndex=" + parentIndex);
        HNLog.d(HNLog.RENDER, "level=" + level);
        HNLog.d(HNLog.RENDER, "stackCss=" + Arrays.toString(parentCssStack));
        HNLog.d(HNLog.RENDER, "stackSize=" + Arrays.toString(lastCssStackSize));
        HNLog.d(HNLog.RENDER, "------------------");
    }


}
