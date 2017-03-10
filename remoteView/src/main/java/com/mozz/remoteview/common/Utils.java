package com.mozz.remoteview.common;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mozz.remoteview.AttrApplyException;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Yang Tao, 17/2/24.
 */

public final class Utils {
    private Utils() {
    }

    public static void closeQuitely(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
                // do nothing
            }
        }
    }

    public static int color(@NonNull Object object) throws AttrApplyException {
        try {
            return Color.parseColor(object.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new AttrApplyException("can't read color from " + object);
        }
    }

    public static int toInt(Object object) throws AttrApplyException {
        if (object instanceof Integer) {
            return (int) object;
        } else {
            throw new AttrApplyException("can't read int from " + object);
        }
    }

    public static float toFloat(Object object) throws AttrApplyException {
        if (object instanceof Float) {
            return (float) object;
        } else if (object instanceof String) {
            try {
                float f = Float.valueOf((String) object);
                return f;
            } catch (NumberFormatException e) {
                throw new AttrApplyException("can't read float from " + object);
            }
        } else {
            throw new AttrApplyException("can't read float from " + object);
        }
    }

    public static float px(Object object) throws AttrApplyException {
        if (object instanceof String) {
            String s = (String) object;

            if (s.endsWith("px")) {
                return toFloat(s.substring(0, s.length() - 2));
            } else {
                return toFloat(s);
            }
        } else {
            return toFloat(object);
        }
    }


    public static boolean toBoolean(Object object) throws AttrApplyException {
        if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            throw new AttrApplyException("can't read boolean from " + object);
        }
    }
}
