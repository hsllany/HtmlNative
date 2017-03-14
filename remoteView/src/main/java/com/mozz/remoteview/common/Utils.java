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

    public static int toInt(Object object) throws AttrApplyException {
        if (object instanceof Integer) {
            return (int) object;
        } else {
            try {
                int i = Integer.parseInt(object.toString());
                return i;
            } catch (NumberFormatException e) {
                throw new AttrApplyException("can't read int from " + object);
            }
        }
    }

    public static float toFloat(Object object) throws AttrApplyException {
        if (object instanceof Float) {
            return (float) object;
        } else {
            try {
                float f = Float.valueOf(object.toString());
                return f;
            } catch (NumberFormatException e) {
                throw new AttrApplyException("can't read float from " + object);
            }
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

    public static int color(@NonNull Object colorObj) throws AttrApplyException {
        String colorString = colorObj.toString();
        if (colorString.charAt(0) == '#') {
            if (colorString.length() > 4) {
                try {
                    return Color.parseColor(colorString);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    throw new AttrApplyException("can't read color from " + colorString);
                }
            } else if (colorString.length() == 4) {
                long color = 0;
                for (int i = 0; i < 3; i++) {
                    char c = colorString.charAt(i + 1);
                    int cI = 0;
                    if (c >= 'a' && c <= 'z') {
                        cI = c - 'a' + 10;
                    } else if (c >= 'A' && c <= 'Z') {
                        cI = c - 'A' + 10;
                    } else if (c >= '0' && c <= '9') {
                        cI = c - '0';
                    }

                    color |= (cI * 16 + cI) << (3 - i - 1) * 8;
                }

                return (int) (color | 0x00000000ff000000);
            } else {
                throw new AttrApplyException("error when parsing color " + colorString);
            }
        } else {
            try {
                return Color.parseColor(colorString);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new AttrApplyException("can't read color from " + colorString);
            }
        }
    }
}
