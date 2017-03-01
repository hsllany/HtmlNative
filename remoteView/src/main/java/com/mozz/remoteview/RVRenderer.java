package com.mozz.remoteview;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mozz.remoteview.common.Performance;
import com.mozz.remoteview.common.PerformanceWatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


final class RVRenderer {

    private static final String TAG = RVRenderer.class.getSimpleName();

    static boolean DEBUG = false;

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    private final Object[] mConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class};

    // for running render task
    private static HandlerThread mProcessThread = new HandlerThread("RVRenderThread");
    private static Handler mProcessHandler;

    static void init() {
        mProcessThread.start();
        mProcessHandler = new Handler(mProcessThread.getLooper());
    }

    static void quit() {
        mProcessThread.quit();
    }

    static void runRenderTask(Runnable r) {
        mProcessHandler.post(r);
    }

    private RVRenderer() {
    }

    public static RVRenderer get() {
        return new RVRenderer();
    }

    public View inflate(Context context, RVModule rvModule, ViewGroup root, boolean attachToRoot,
                        ViewGroup.LayoutParams params) throws RemoteInflateException {

        PerformanceWatcher pWatcher = Performance.newWatcher();
        FrameLayout frameLayout = new FrameLayout(context);
        ViewContext viewContext = ViewContext.initViewContext(frameLayout, rvModule, context);
        pWatcher.check("[step 1] create ViewContext");

        viewContext.onViewCreate();
        pWatcher.check("[step 2] call onViewCreate");

        View v = inflate(context, viewContext, rvModule.mRootTree, rvModule.mAttrs, root,
                attachToRoot, params);
        pWatcher.check("[step 3] rendering view");

        if (v == null)
            return null;
        frameLayout.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        viewContext.onViewLoaded();
        pWatcher.checkDone("finally done");

        if (DEBUG) {
            Log.d(TAG, viewContext.mViewSelector.toString());
        }

        return frameLayout;
    }

    private View inflate(Context context, ViewContext viewContext, RVDomTree tree,
                         AttrsSet attrsSet, ViewGroup root, boolean attachToRoot,
                         ViewGroup.LayoutParams params) throws RemoteInflateException {

        View result = root;

        if (tree.isLeaf()) {
            return createViewFromTag(tree, viewContext, tree.getNodeName(), ANDROID_VIEW_PREFIX,
                    context, attrsSet, params);
        } else {
            View view = createViewFromTag(tree, viewContext, tree.getNodeName(), ANDROID_VIEW_PREFIX,
                    context, attrsSet, params);

            if (view == null && attachToRoot) {
                return root;
            } else if (view == null) {
                return null;
            }

            if (view instanceof ViewGroup) {

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                ViewGroup viewGroup = (ViewGroup) view;

                for (RVDomTree child : tree.mChildren) {
                    View v = inflate(context, viewContext, child, attrsSet, null, false,
                            layoutParams);
                    viewGroup.addView(v, layoutParams);
                }
            } else {
                Log.w(TAG, "View inflate from RVRenderer is not an viewGroup" +
                        view.getClass().getSimpleName() +
                        ", but related RVDomTree has children. Will ignore its children!");
            }

            if (root != null && attachToRoot) {
                root.addView(view, params);
            }

            if (root == null || !attachToRoot) {
                result = view;
            }

            return result;
        }
    }

    private static final String ANDROID_VIEW_PREFIX = "android.widget.";

    private View createViewFromTag(RVDomTree tree, ViewContext viewContext, String name,
                                   String prefix, Context context, AttrsSet attrsSet,
                                   ViewGroup.LayoutParams params) {

        long time1 = SystemClock.currentThreadTimeMillis();

        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            Class<? extends View> clazz;
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);


                constructor = clazz.getConstructor(sConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name, constructor);
            }

            mConstructorArgs[0] = context;
            final View view = constructor.newInstance(mConstructorArgs);
            if (DEBUG) {
                Log.d(TAG, "create view " + view.toString());
            }
            try {
                attrsSet.apply(context, viewContext, view, tree, params);
            } catch (AttrsSet.AttrApplyException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "create view " + prefix + name + " spend " + (SystemClock.currentThreadTimeMillis() - time1) + " ms");
            return view;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

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

    interface RVViewInflateListener {
        void onViewLoaded();
    }

}
