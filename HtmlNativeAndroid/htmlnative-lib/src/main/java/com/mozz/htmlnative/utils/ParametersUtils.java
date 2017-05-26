package com.mozz.htmlnative.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.mozz.htmlnative.common.ContextProvider;
import com.mozz.htmlnative.common.PixelValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Yang Tao, 17/2/24.
 */

public final class ParametersUtils {

    private static final String TAG = ParametersUtils.class.getSimpleName();

    private static final Set<String> sColorNameMap;

    static {
        sColorNameMap = new HashSet<>();
        sColorNameMap.add("black");
        sColorNameMap.add("darkgray");
        sColorNameMap.add("gray");
        sColorNameMap.add("lightgray");
        sColorNameMap.add("white");
        sColorNameMap.add("red");
        sColorNameMap.add("green");
        sColorNameMap.add("blue");
        sColorNameMap.add("yellow");
        sColorNameMap.add("cyan");
        sColorNameMap.add("magenta");
        sColorNameMap.add("aqua");
        sColorNameMap.add("fuchsia");
        sColorNameMap.add("darkgrey");
        sColorNameMap.add("grey");
        sColorNameMap.add("lightgrey");
        sColorNameMap.add("lime");
        sColorNameMap.add("maroon");
        sColorNameMap.add("navy");
        sColorNameMap.add("olive");
        sColorNameMap.add("purple");
        sColorNameMap.add("silver");
        sColorNameMap.add("teal");

    }

    private ParametersUtils() {
    }

    private static float screenDensity = -1.f;

    public static void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        updateScreenDensity(density);
    }

    private static void updateScreenDensity(float density) {
        screenDensity = density;
    }

    public static int toInt(Object object) throws IllegalArgumentException {
        if (object instanceof Integer) {
            return (int) object;
        } else {
            return Integer.valueOf(object.toString());
        }
    }

    public static float toFloat(Object object) throws IllegalArgumentException {
        if (object instanceof Float) {
            return (float) object;
        } else {
            String fStr = object.toString();
            boolean isPercentage = false;
            if (fStr.endsWith("%")) {
                fStr = fStr.substring(0, fStr.length() - 1);
                isPercentage = true;
            }
            float f = Float.valueOf(fStr);

            return isPercentage ? f / 100 : f;

        }
    }

    public static PixelValue toPixel(Object object) throws IllegalArgumentException {
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

            unit = getUnit(unitString.reverse().toString());

            float value = toFloat(string.substring(0, i + 1));
            return new PixelValue(value, unit);

        } else {
            return new PixelValue(toFloat(object), unit);
        }
    }

    @PixelValue.PixelUnit
    public static int getUnit(String s) {
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

    public static float getPercent(String s) throws IllegalArgumentException {
        if (s.endsWith("%")) {
            return toInt(s.substring(0, s.length() - 1)) / 100.f;
        } else {
            throw new IllegalArgumentException("not a percent format " + s);
        }
    }

    public static PixelValue[] toPixels(String ss) throws IllegalArgumentException {
        String[] single = splitByEmpty(ss);

        PixelValue[] pixelValues = new PixelValue[single.length];

        int i = 0;

        for (String s : single) {
            String trimS = s.trim();

            pixelValues[i++] = toPixel(trimS);
        }

        return pixelValues;
    }


    public static boolean toBoolean(Object object) throws IllegalArgumentException {
        if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            return Boolean.valueOf(object.toString().trim());
        }
    }

    public static int toColor(@NonNull Object colorObj) throws IllegalArgumentException {
        String colorString = colorObj.toString().trim();
        if (colorString.length() == 0) {
            throw new IllegalArgumentException("empty color string for parse");
        }

        // handle the #* like color
        if (colorString.charAt(0) == '#') {

            // handle the #000000 like color string
            if (colorString.length() > 4) {
                return Color.parseColor(colorString);
            } else if (colorString.length() == 4) {
                long color = 0;
                for (int i = 0; i < 3; i++) {
                    char c = colorString.charAt(i + 1);
                    int cI;
                    if (c >= 'a' && c <= 'z') {
                        cI = c - 'a' + 10;
                    } else if (c >= 'A' && c <= 'Z') {
                        cI = c - 'A' + 10;
                    } else if (c >= '0' && c <= '9') {
                        cI = c - '0';
                    } else {
                        throw new IllegalArgumentException("unknown color string " + colorString);
                    }

                    color |= (cI * 16 + cI) << (3 - i - 1) * 8;
                }

                return (int) (color | 0x00000000ff000000);
            } else {
                throw new IllegalArgumentException("unknown color string " + colorString);
            }

        } else if (colorString.charAt(0) == '@') {
            String colorRes = colorString.substring(1);
            Context context = ContextProvider.getApplicationRef();
            if (context != null) {
                return ResourceUtils.getColor(colorRes, context);
            } else {
                return Color.BLACK;
            }
        } else {
            /**
             handle the color like 'red', 'green' ect. see {@link https://www.w3.org/TR/CSS2/syndata
            .html#tokenization}
             */
            return Color.parseColor(colorString);

        }
    }

    public static String toHtmlColorString(int color) {
        return "#" + Integer.toHexString(color & 0x00ffffff);
    }

    public static boolean isHtmlColorString(String string) {
        return sColorNameMap.contains(string);
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

    public static float dpToPx(float px) {
        if (screenDensity == -1.f) {
            throw new IllegalStateException("you must call init() first");
        }
        return (int) (screenDensity * px + 0.5f);
    }

    public static float pxToDp(float dp) {
        if (screenDensity == -1.f) {
            throw new IllegalStateException("you must call init() first");
        }
        return dp / screenDensity;
    }

    public static int emToPx(float em) {
        return (int) (em * 16.f);
    }

    public static float pxToEm(int px) {
        return px / 16.f;
    }

    public static String[] splitByEmpty(String s) {
        return s.trim().split("\\s+");
    }
}
