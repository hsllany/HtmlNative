package com.mozz.htmlnative.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.reflect.Field;

/**
 * @author Yang Tao, 17/5/25.
 */

public class ResourceUtils {
    private ResourceUtils() {

    }

    public static String getString(String id, Context context) {
        try {
            Class<?> rClass = getClass(context, ResourceType.String);
            int idd = getId(id, rClass);
            if (idd != View.NO_ID) {
                return context.getString(idd);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static int getColor(String id, Context context) {
        try {
            Class<?> rClass = getClass(context, ResourceType.Color);
            int idd = getId(id, rClass);
            if (idd != View.NO_ID) {
                return context.getColor(idd);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return Color.TRANSPARENT;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return Color.TRANSPARENT;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return Color.TRANSPARENT;
        }

        return Color.TRANSPARENT;
    }

    public static Drawable getDrawable(String id, Context context) {
        try {
            Class<?> rClass = getClass(context, ResourceType.Drawable);
            int idd = getId(id, rClass);
            if (idd != View.NO_ID) {
                return context.getDrawable(idd);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static float getDimension(String id, Context context) {
        try {
            Class<?> rClass = getClass(context, ResourceType.Dimension);
            int idd = getId(id, rClass);
            if (idd != View.NO_ID) {
                return context.getResources().getDimension(idd);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return 0;
        }

        return 0;
    }

    private static int getId(String id, Class<?> rClass) throws NoSuchFieldException,
            IllegalAccessException {
        Field f = rClass.getField(id);
        if (f != null) {
            Object r = f.get(rClass);
            if (r instanceof Integer) {
                return (Integer) r;
            }
        }

        return View.NO_ID;
    }

    private static Class<?> getClass(Context context, ResourceType type) throws
            ClassNotFoundException {

        switch (type) {
            case String:
                return getRStringClass(context);
            case Color:
                return getRColorClass(context);
            case Drawable:
                return getRDrawableClass(context);
            case Dimension:
                return getRDimensionClass(context);
        }

        return null;
    }

    private static Class<?> getRStringClass(Context context) throws ClassNotFoundException {
        return Class.forName(context.getPackageName() + ".R$string");
    }

    private static Class<?> getRColorClass(Context context) throws ClassNotFoundException {
        return Class.forName(context.getPackageName() + ".R$color");
    }

    private static Class<?> getRDrawableClass(Context context) throws ClassNotFoundException {
        return Class.forName(context.getPackageName() + ".R$drawable");
    }

    private static Class<?> getRDimensionClass(Context context) throws ClassNotFoundException {
        return Class.forName(context.getPackageName() + ".R$dimen");
    }

    private enum ResourceType {
        String, Color, Drawable, Dimension
    }
}
