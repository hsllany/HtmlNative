package com.mozz.htmlnative.common;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;

import com.mozz.htmlnative.attrs.AttrApplyException;
import com.mozz.htmlnative.attrs.PixelValue;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Tao, 17/2/24.
 */

public final class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private Utils() {
    }

    public static void closeQuietly(@Nullable Closeable closeable) {
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
                int i = Integer.valueOf(object.toString());
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

    public static PixelValue toPixel(Object object) throws AttrApplyException {
        int unit = TypedValue.COMPLEX_UNIT_PX;
        if (object instanceof String) {
            String string = (String) object;

            StringBuilder unitString = new StringBuilder();
            int i = string.length() - 1;
            for (; i > 0; i--) {
                char c = string.charAt(i);
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    unitString.append(c);
                } else {
                    break;
                }
            }

            try {
                unit = getUnit(unitString.reverse().toString());
            } catch (AttrApplyException e) {
            }

            float value = toFloat(string.substring(0, i + 1));
            return new PixelValue(value, unit);

        } else {
            return new PixelValue(toFloat(object), unit);
        }
    }

    public static int getUnit(String s) throws AttrApplyException {
        switch (s.toLowerCase()) {
            case "px":
                return TypedValue.COMPLEX_UNIT_PX;
            case "dp":
            case "dip":
                return TypedValue.COMPLEX_UNIT_DIP;
            case "sp":
                return TypedValue.COMPLEX_UNIT_SP;
            case "em":
                return PixelValue.EM;
            default:
                return PixelValue.UNSET;

        }
    }

    public static PixelValue[] pixelPairs(String ss) throws AttrApplyException {
        String[] single = ss.split(" ");

        PixelValue[] pixelValues = new PixelValue[single.length];

        int i = 0;

        for (String s : single) {
            String trimS = s.trim();

            pixelValues[i++] = toPixel(trimS);
        }

        return pixelValues;
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
        String colorString = colorObj.toString().trim();
        if (colorString.length() == 0) {
            Log.e(TAG, "empty color string for parse");
            throw new AttrApplyException("empty color string for parse");
        }
        if (colorString.charAt(0) == '#') {
            if (colorString.length() > 4) {
                try {
                    return Color.parseColor(colorString);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "IllegalArgumentException" + e.getMessage() + " can't read color " +
                            "from");
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


    public static Map<String, String> parseStyle(@NonNull String styleString) {
        Map<String, String> pas = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String key = null;
        for (int i = 0; i < styleString.length(); i++) {
            char c = styleString.charAt(i);

            if (c == ';') {
                pas.put(key, sb.toString());
                sb.setLength(0);
            } else if (c == ':') {
                key = sb.toString();
                sb.setLength(0);
            } else {
                if (c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\f' || c == '\b') {
                    continue;
                }
                sb.append(c);
            }
        }

        if (key != null) {
            pas.put(key, sb.toString());
        }

        return pas;
    }
}
