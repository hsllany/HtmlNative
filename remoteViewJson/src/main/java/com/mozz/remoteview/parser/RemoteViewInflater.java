package com.mozz.remoteview.parser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by Yang Tao on 17/2/22.
 */

public class RemoteViewInflater {

    private static final String TAG = RemoteViewInflater.class.getSimpleName();
    private static boolean DEBUG = false;

    private Context mContext;

    private boolean mFactorySet;
    private LayoutInflater.Factory mFactory;
    private LayoutInflater.Factory2 mFactory2;
    private LayoutInflater.Factory2 mPrivateFactory;

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    final Object[] mConstructorArgs = new Object[1];

    static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class};

    protected RemoteViewInflater(Context context) {
        mContext = context;
    }

    public static RemoteViewInflater from(@NonNull Context context) {
        return new RemoteViewInflater(context);
    }

    public View inflate(Context context, @NonNull SyntaxTree tree, ViewGroup root, boolean attachToRoot, ViewGroup.LayoutParams params) throws RemoteInflateException {

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

                for (SyntaxTree child : tree.mChildren) {
                    View v = inflate(context, child, null, false, layoutParams);
                    viewGroup.addView(v, layoutParams);
                }
            } else {
                Log.w(TAG, "View inflate from RemoteViewInflater is not an viewGroup" +
                        view.getClass().getSimpleName() +
                        ", but related SyntaxTree has children. Will ignore its children!");
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

            attrsSet.apply(context, view, params);
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
