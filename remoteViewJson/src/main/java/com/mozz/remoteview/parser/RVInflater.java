package com.mozz.remoteview.parser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


public class RVInflater {

    private static final String TAG = RVInflater.class.getSimpleName();
    static boolean DEBUG = false;

    private Context mContext;

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    private final Object[] mConstructorArgs = new Object[1];

    private static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class};

    private RVInflater(Context context) {
        mContext = context;
    }

    public static RVInflater from(@NonNull Context context) {
        return new RVInflater(context);
    }

    public View inflate(Context context, RVContext rvContext, ViewGroup root, boolean attachToRoot, ViewGroup.LayoutParams params) throws RemoteInflateException {
        return inflate(context, rvContext.mRootTree, root, attachToRoot, params);
    }

    protected View inflate(Context context, @NonNull RVDomTree tree, ViewGroup root, boolean attachToRoot, ViewGroup.LayoutParams params) throws RemoteInflateException {

        View result = root;

        if (tree.isLeaf()) {
            return createViewFromTag(tree.getNodeName(), ANDROID_VIEW_PREFIX, context, tree.mAttrs, params);
        } else {
            View view = createViewFromTag(tree.getNodeName(), ANDROID_VIEW_PREFIX, context, tree.mAttrs, params);

            if (view == null && attachToRoot) {
                return root;
            } else if (view == null) {
                return null;
            }

            if (view instanceof ViewGroup) {

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                ViewGroup viewGroup = (ViewGroup) view;

                for (RVDomTree child : tree.mChildren) {
                    View v = inflate(context, child, null, false, layoutParams);
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

    protected View createViewFromTag(String name, String prefix, Context context, AttrsSet attrsSet, ViewGroup.LayoutParams params) {
        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            Class<? extends View> clazz;
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                clazz = mContext.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);


                constructor = clazz.getConstructor(sConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name, constructor);
            }

            mConstructorArgs[0] = context;
            final View view = constructor.newInstance(mConstructorArgs);

            try {
                attrsSet.apply(context, view, params);
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

}
