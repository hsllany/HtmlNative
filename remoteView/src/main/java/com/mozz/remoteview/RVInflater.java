package com.mozz.remoteview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


public final class RVInflater {

    private static final String TAG = RVInflater.class.getSimpleName();
    static boolean DEBUG = false;

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    private final Object[] mConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class};

    private RVInflater() {
    }

    public static RVInflater get() {
        return new RVInflater();
    }

    public View inflate(Context context, RVModule rvModule, ViewGroup root, boolean attachToRoot,
                        ViewGroup.LayoutParams params) throws RemoteInflateException {
        FrameLayout frameLayout = new FrameLayout(context);
        ViewContext viewContext = ViewContext.initViewContext(frameLayout, rvModule, context);

        viewContext.onViewCreate();

        View v = inflate(context, viewContext, rvModule.mRootTree, rvModule.mAttrs, root,
                attachToRoot, params);

        if (v == null)
            return null;

        frameLayout.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        viewContext.onViewLoaded();

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
                Log.w(TAG, "View inflate from RVInflater is not an viewGroup" +
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
