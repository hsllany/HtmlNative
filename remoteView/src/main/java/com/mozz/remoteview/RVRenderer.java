package com.mozz.remoteview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mozz.remoteview.common.Performance;
import com.mozz.remoteview.common.PerformanceWatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


final class RVRenderer {

    private final static Map<String, String> sTagMap = new ArrayMap<>();

    static {
        sTagMap.put("text", "android.widget.TextView");
        sTagMap.put("image", "android.widget.ImageView");
        sTagMap.put("input", "android.widget.EditText");
        sTagMap.put("button", "android.widget.Button");
        sTagMap.put("linearbox", "android.widget.LinearLayout");
        sTagMap.put("flexbox", "android.widget.LinearLayout");
        sTagMap.put("scroller", "android.widget.ScrollView");
        sTagMap.put("box", "android.widget.AbsoluteLayout");
    }

    private static final String TAG = RVRenderer.class.getSimpleName();

    static boolean DEBUG = false;

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<>();

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

    View inflate(Context context, RVModule rvModule, ViewGroup root, boolean attachToRoot,
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
            return createViewFromTag(tree, viewContext, tree.getNodeName(),
                    context, attrsSet, params);
        } else {
            View view = createViewFromTag(tree, viewContext, tree.getNodeName(),
                    context, attrsSet, params);

            if (view == null && attachToRoot) {
                return root;
            } else if (view == null) {
                return null;
            }


            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;

                for (RVDomTree child : tree.mChildren) {

                    ViewGroup.LayoutParams layoutParams;
                    if (view instanceof AbsoluteLayout) {
                        layoutParams = new AbsoluteLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0);
                    } else {
                        layoutParams = new ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                    }


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

    private View createViewFromTag(RVDomTree tree, ViewContext viewContext, String name,
                                   Context context, AttrsSet attrsSet,
                                   ViewGroup.LayoutParams params) throws RemoteInflateException {

        PerformanceWatcher watcher = Performance.newWatcher();
        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            Class<? extends View> clazz;
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                String viewClassName = sTagMap.get(name);
                if (viewClassName == null)
                    throw new ClassNotFoundException("can't find related widget " + name);

                clazz = context.getClassLoader().loadClass(viewClassName).asSubclass(View.class);

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

            watcher.checkDone("create view " + view.toString());
            return view;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class not found " + name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor is missing " + name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be accessed " + name);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's constructor can not be invoked " + name);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RemoteInflateException("class's method has something wrong " + name);
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

}
