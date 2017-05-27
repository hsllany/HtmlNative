package com.mozz.htmlnative.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.mozz.htmlnative.common.ContextProvider;
import com.mozz.htmlnative.common.PixelValue;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang Tao, 17/2/24.
 */

public final class ParametersUtils {

    private static final Set<String> sColorNameMap;

    private static final int DEFAULT_COLOR = Color.TRANSPARENT;

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

    private static float density = -1.f;
    private static float scaledDensity = -1.f;

    public static void init(@NonNull Context context) {
        density = context.getResources().getDisplayMetrics().density;
        scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
    }

    static void init(float ds, float sDs) {
        density = ds;
        scaledDensity = sDs;
    }


    public static int toInt(Object object) throws ParametersParseException {
        if (object instanceof Integer) {
            return (int) object;
        } else {
            try {
                return Integer.valueOf(object.toString().trim());
            } catch (IllegalArgumentException e) {
                throw new ParametersParseException(e);
            }
        }
    }


    public static float toFloat(Object object) throws ParametersParseException {
        if (object instanceof Float) {
            return (float) object;
        } else {
            String fStr = object.toString();
            boolean isPercentage = false;
            if (fStr.endsWith("%")) {
                isPercentage = true;
            }
            if (isPercentage && fStr.length() > 1) {
                fStr = fStr.substring(0, fStr.length() - 1);
            }
            try {
                float f = Float.valueOf(fStr.trim());
                return isPercentage ? f / 100 : f;
            } catch (IllegalArgumentException e) {
                throw new ParametersParseException(e);
            }

        }
    }

    @NonNull
    public static PixelValue toPixel(@NonNull Object object) throws ParametersParseException {
        int unit = TypedValue.COMPLEX_UNIT_PX;
        if (object instanceof String) {
            String string = (String) object;

            if (string.length() == 0 || (string.equals("@"))) {
                throw new ParametersParseException("wrong when parse pixel");
            }

            if (string.charAt(0) == '@' && string.length() > 1) {
                String resId = string.substring(1);
                Context context = ContextProvider.getApplicationRef();
                if (context != null) {
                    float size = ResourceUtils.getDimension(resId, ContextProvider
                            .getApplicationRef());
                    return new PixelValue(size);
                } else {
                    throw new ParametersParseException("wrong when parse pixel");
                }
            }

            StringBuilder unitString = new StringBuilder(5);
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

            float value = 0;
            value = toFloat(string.substring(0, i + 1));
            return new PixelValue(value, unit);
        } else {
            return new PixelValue(toFloat(object), unit);
        }

    }

    @PixelValue.PixelUnit
    private static int getUnit(String s) throws ParametersParseException {
        switch (s.toLowerCase()) {
            default:
                throw new ParametersParseException("Unknown unit " + s);
            case "px":
                return TypedValue.COMPLEX_UNIT_PX;
            case "dp":
            case "dip":
                return TypedValue.COMPLEX_UNIT_DIP;
            case "sp":
                return TypedValue.COMPLEX_UNIT_SP;
            case "em":
                return PixelValue.EM;

        }
    }

    public static float getPercent(String s) throws ParametersParseException {
        if (s.endsWith("%")) {
            return toInt(s.substring(0, s.length() - 1)) / 100.f;
        } else {
            throw new ParametersParseException("not a percent format " + s);
        }
    }

    public static PixelValue[] toPixels(String ss) throws ParametersParseException {
        String[] single = splitByEmpty(ss);

        PixelValue[] pixelValues = new PixelValue[single.length];

        int i = 0;

        for (String s : single) {
            String trimS = s.trim();
            pixelValues[i++] = toPixel(trimS);
        }

        return pixelValues;
    }


    public static boolean toBoolean(Object object) throws ParametersParseException {
        if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            try {
                return Boolean.valueOf(object.toString().trim());
            } catch (IllegalArgumentException e) {
                throw new ParametersParseException(e);
            }
        }
    }

    public static int toColor(@NonNull Object colorObj) throws ParametersParseException {
        String colorString = colorObj.toString().trim();
        if (colorString.length() == 0) {
            throw new ParametersParseException("empty color string for parse");
        }

        // handle the #* like color
        if (colorString.charAt(0) == '#') {

            // handle the #000000 like color string
            if (colorString.length() == 7 || colorString.length() == 9) {
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
                        throw new ParametersParseException("unknown color string " + colorString);
                    }

                    color |= (cI * 16 + cI) << (3 - i - 1) * 8;
                }

                return (int) (color | 0x00000000ff000000);
            } else {
                throw new ParametersParseException("unknown color string " + colorString);
            }

        } else if (colorString.charAt(0) == '@' && colorString.length() > 1) {
            String colorRes = colorString.substring(1);
            Context context = ContextProvider.getApplicationRef();
            if (context != null) {
                return ResourceUtils.getColor(colorRes, context);
            } else {
                return DEFAULT_COLOR;
            }
        } else {
            /**
             handle the color like 'red', 'green' ect. see {@link https://www.w3.org/TR/CSS2/syndata
            .html#tokenization}
             */
            try {
                return Color.parseColor(colorString);
            } catch (IllegalArgumentException e) {
                throw new ParametersParseException(e);
            }

        }
    }

    public static String toHtmlColorString(int color) {
        return "#" + Integer.toHexString(color & 0x00ffffff);
    }

    public static boolean isHtmlColorString(String string) {
        return sColorNameMap.contains(string);
    }

    public static float dpToPx(float dp) {
        if (density == -1.f) {
            throw new IllegalStateException("you must call init() first");
        }
        return (int) (density * dp + 0.5f);
    }

    public static float pxToDp(float px) {
        if (density == -1.f) {
            throw new IllegalStateException("you must call init() first");
        }
        return (int) (px / density + 0.5f);
    }

    public static float spToPx(float sp) {
        if (scaledDensity == -1.f) {
            throw new IllegalStateException("you must call init() first");
        }
        return (int) (scaledDensity * sp + 0.5f);
    }

    public static float pxToSp(float px) {
        if (scaledDensity == -1.f) {
            throw new IllegalStateException("you must call init() first");
        }
        return (int) (px / scaledDensity + 0.5f);
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

    public static class ParametersParseException extends Exception {
        private ParametersParseException() {
            super();
        }

        private ParametersParseException(String msg) {
            super(msg);
        }

        private ParametersParseException(Throwable cause) {
            super(cause);
        }
    }
}
