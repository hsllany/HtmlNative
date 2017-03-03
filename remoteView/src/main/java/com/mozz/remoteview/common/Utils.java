package com.mozz.remoteview.common;

import android.graphics.Color;

import com.mozz.remoteview.AttrApplyException;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Yang Tao, 17/2/24.
 */

public final class Utils {
    private Utils() {
    }

    public static void closeQuitely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
                // do nothing
            }
        }
    }

    public static int color(Object object) throws AttrApplyException {
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
        } else {
            throw new AttrApplyException("can't read float from " + object);
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
