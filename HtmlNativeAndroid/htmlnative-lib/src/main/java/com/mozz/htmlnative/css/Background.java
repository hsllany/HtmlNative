package com.mozz.htmlnative.css;

import android.graphics.Color;
import android.graphics.Matrix;
import android.text.TextUtils;

import static com.mozz.htmlnative.utils.ParametersUtils.ParametersParseException;
import static com.mozz.htmlnative.utils.ParametersUtils.getPercent;
import static com.mozz.htmlnative.utils.ParametersUtils.isHtmlColorString;
import static com.mozz.htmlnative.utils.ParametersUtils.splitByEmpty;
import static com.mozz.htmlnative.utils.ParametersUtils.toColor;
import static com.mozz.htmlnative.utils.ParametersUtils.toHtmlColorString;
import static com.mozz.htmlnative.utils.ParametersUtils.toPixel;

/**
 * @author Yang Tao, 17/3/24.
 */

public class Background {

    public static final int REPEAT = 0x00000001;
    public static final int REPEAT_X = 0x00000002;
    public static final int REPEAT_Y = 0x00000003;
    public static final int NO_REPEAT = 0x00000004;

    public static final int LENGTH = 0x00000001;
    public static final int PERCENTAGE = 0x00000002;
    public static final int AUTO = 0x00000003;

    private static final int LK_COLOR = 0;
    private static final int LK_URL = 1;
    private static final int LK_REPEAT = 2;
    private static final int LK_X = 3;
    private static final int LK_Y = 4;
    private static final int LK_WIDTH = 5;
    private static final int LK_HEIGHT = 6;

    private String url = "";
    private int color = Color.TRANSPARENT;
    private boolean colorSet = false;
    private int repeat = REPEAT;
    private float x;
    private float y;
    private float width;
    private float height;

    private float colorWidth = 1.f;
    private float colorHeight = 1.f;

    private int colorWidthMode = PERCENTAGE;
    private int colorHeightMode = PERCENTAGE;

    private int xMode = PERCENTAGE;
    private int yMode = PERCENTAGE;
    private int widthMode = AUTO;
    private int heightMode = AUTO;

    private boolean isAndroidResource = false;


    @Override
    public String toString() {
        String widthStr = widthMode == AUTO ? "auto" : (widthMode == PERCENTAGE ? width + "%" :
                width + "");
        String heightStr = heightMode == AUTO ? "auto" : (heightMode == PERCENTAGE ? height + "%"
                : height + "");
        String xStr = xMode == PERCENTAGE ? (x * 100) + "%" : x + "";
        String yStr = yMode == PERCENTAGE ? (y * 100) + "%" : y + "";
        String urlStr = !TextUtils.isEmpty(url) ? " url(" + url + ")" : "";

        return "background:" + toHtmlColorString(color) + urlStr + " " + xStr + "" + " " + yStr +
                " / " + widthStr + " " + heightStr + " " + repeatToString(repeat);
    }

    public void setColor(int color) {
        this.color = color;
        this.colorSet = true;
    }

    public boolean isColorSet() {
        return this.colorSet;
    }

    public int getColor() {
        return this.color;
    }

    public void setUrl(String url) {
        if (url.charAt(0) == '@') {
            this.isAndroidResource = true;
        }
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getXMode() {
        return xMode;
    }

    public int getYMode() {
        return yMode;
    }

    public int getWidthMode() {
        return widthMode;
    }

    public int getHeightMode() {
        return heightMode;
    }

    public static Background createOrChange(String param, String val, Object oldOne) {
        Background style;
        if (oldOne == null) {
            style = new Background();
        } else {
            style = (Background) oldOne;
        }

        String[] subStrings = splitByEmpty(val);

        switch (param) {
            case "background": {
                int lk = LK_COLOR;
                for (String item : subStrings) {

                    if (item.equals("/")) {
                        lk = LK_WIDTH;
                        continue;
                    }

                    if (item.startsWith("url(") && lk <= LK_URL) {
                        style.setUrl(item.substring(item.indexOf('(') + 1, item.lastIndexOf(')'))
                                .trim());
                        lk = LK_URL + 1;

                    } else if (item.startsWith("#") || isHtmlColorString(item)) {
                        try {
                            style.setColor(toColor(item));
                        } catch (ParametersParseException e) {
                            e.printStackTrace();
                        }
                    } else if (item.equals("no-repeat") || item.equals("repeat-x") || item.equals
                            ("repeat-y") || item.equals("repeat") && lk <= LK_REPEAT) {
                        switch (item) {
                            case "repeat":
                                style.setRepeat(REPEAT);
                                break;
                            case "repeat-x":
                                style.setRepeat(REPEAT_X);
                                break;
                            case "repeat-y":
                                style.setRepeat(REPEAT_Y);
                                break;
                            case "no-repeat":
                                style.setRepeat(NO_REPEAT);
                                break;
                            default:

                        }
                        lk = LK_REPEAT + 1;

                    } else if (lk >= LK_URL) {
                        try {
                            if (item.endsWith("%")) {
                                try {
                                    style.setX(getPercent(item));
                                    style.xMode = PERCENTAGE;
                                } catch (ParametersParseException ignored) {

                                }
                            } else if (item.equals("left")) {
                                style.setX(0f);
                                style.xMode = PERCENTAGE;
                            } else if (item.equals("right")) {
                                style.setX(1);
                                style.xMode = PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setX(0.5f);
                                style.xMode = PERCENTAGE;
                            } else {
                                style.setX(toPixel(item).getPxValue());
                                style.xMode = LENGTH;
                            }
                        } catch (ParametersParseException e) {
                            e.printStackTrace();
                        }

                        lk++;

                    } else if (lk == LK_Y) {
                        try {
                            if (item.endsWith("%")) {
                                try {
                                    style.setY(getPercent(item));
                                    style.yMode = PERCENTAGE;
                                } catch (ParametersParseException ignored) {

                                }
                            } else if (item.equals("top")) {
                                style.setY(0f);
                                style.yMode = PERCENTAGE;
                            } else if (item.equals("bottom")) {
                                style.setY(1f);
                                style.yMode = PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setY(0.5f);
                                style.yMode = PERCENTAGE;
                            } else {
                                style.setY(toPixel(item).getPxValue());
                                style.yMode = LENGTH;
                            }
                        } catch (ParametersParseException e) {
                            e.printStackTrace();
                        }

                        lk++;

                    } else if (lk == LK_WIDTH) {
                        try {
                            if (item.endsWith("%")) {
                                style.width = getPercent(item);
                                style.widthMode = PERCENTAGE;

                            } else if (item.equals("auto")) {
                                style.width = 0;
                                style.widthMode = AUTO;
                            } else {
                                style.width = toPixel(item).getPxValue();
                                style.widthMode = LENGTH;
                            }
                        } catch (ParametersParseException e) {
                            e.printStackTrace();
                        }
                        lk++;
                    } else if (lk == LK_HEIGHT) {
                        try {
                            if (item.endsWith("%")) {

                                style.height = getPercent(item);
                                style.heightMode = PERCENTAGE;

                            } else if (item.equals("auto")) {
                                style.height = 0;
                                style.heightMode = AUTO;
                            } else {
                                style.height = (float) toPixel(item).getPxValue();
                                style.heightMode = LENGTH;
                            }
                        } catch (ParametersParseException e) {
                            e.printStackTrace();
                        }

                        lk++;
                    }
                }
            }
            break;

            case "background-color": {
                if (subStrings.length >= 1) {
                    try {
                        style.setColor(toColor(subStrings[0]));
                    } catch (ParametersParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;

            case "background-image": {
                if (subStrings.length >= 1) {
                    String image = subStrings[0];
                    if (image.startsWith("url(")) {
                        int parenStart = image.indexOf('(');
                        int parenEnd = image.indexOf(')');
                        style.setUrl(image.substring(parenStart + 1, parenEnd).trim());
                    }
                }
            }
            break;

            case "background-position": {

                int lookFor = LK_X;
                for (String item : subStrings) {
                    try {
                        if (lookFor == LK_X) {
                            if (item.endsWith("%")) {
                                style.xMode = PERCENTAGE;
                                style.setX(getPercent(item));
                            } else if (item.equals("left")) {
                                style.setX(0f);
                                style.xMode = PERCENTAGE;
                            } else if (item.equals("right")) {
                                style.setX(1.f);
                                style.xMode = PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setX(0.5f);
                                style.xMode = PERCENTAGE;
                            } else {
                                style.xMode = LENGTH;
                                style.setX(toPixel(item).getPxValue());
                            }

                        } else if (lookFor == LK_Y) {

                            if (item.endsWith("%")) {
                                style.yMode = PERCENTAGE;
                                style.setY(getPercent(item));
                            } else if (item.equals("top")) {
                                style.setY(0f);
                                style.yMode = PERCENTAGE;
                            } else if (item.equals("bottom")) {
                                style.setY(1.f);
                                style.yMode = PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setY(0.5f);
                                style.yMode = PERCENTAGE;
                            } else {
                                style.yMode = LENGTH;
                                style.setY(toPixel(item).getPxValue());
                            }

                        } else {
                            break;
                        }
                    } catch (ParametersParseException e) {
                        e.printStackTrace();
                    }

                    lookFor++;
                }
            }
            break;

            case "background-repeat": {
                if (subStrings.length >= 1) {
                    String repeat = subStrings[0];

                    switch (repeat) {
                        case "repeat":
                            style.setRepeat(REPEAT);
                            break;
                        case "repeat-x":
                            style.setRepeat(REPEAT_X);
                            break;
                        case "repeat-y":
                            style.setRepeat(REPEAT_Y);
                            break;
                        case "no-repeat":
                            style.setRepeat(NO_REPEAT);
                            break;
                        default:

                    }
                }
            }
            break;

            case "background-size": {
                if (subStrings.length >= 2) {
                    String width = subStrings[0];
                    String height = subStrings[1];
                    try {
                        if (width.endsWith("%")) {
                            style.width = getPercent(width);
                            style.widthMode = PERCENTAGE;
                        } else if (width.equals("auto")) {
                            style.width = 0;
                            style.widthMode = AUTO;
                        } else {
                            style.width = toPixel(width).getPxValue();
                            style.widthMode = LENGTH;
                        }

                        if (height.endsWith("%")) {
                            style.height = getPercent(height);
                            style.heightMode = PERCENTAGE;
                        } else if (height.equals("auto")) {
                            style.height = 0;
                            style.heightMode = AUTO;
                        } else {
                            style.height = toPixel(height).getPxValue();
                            style.heightMode = LENGTH;
                        }
                    } catch (ParametersParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;

            case "-hn-background-color-size": {
                if (subStrings.length >= 2) {
                    String width = subStrings[0];
                    String height = subStrings[1];
                    try {
                        if (width.endsWith("%")) {
                            style.colorWidth = getPercent(width);
                            style.colorWidthMode = PERCENTAGE;
                        } else {
                            style.colorWidth = toPixel(width).getPxValue();
                            style.colorWidthMode = LENGTH;
                        }

                        if (height.endsWith("%")) {
                            style.colorHeight = getPercent(height);
                            style.colorHeightMode = PERCENTAGE;

                        } else {
                            style.colorHeight = toPixel(height).getPxValue();
                            style.colorHeightMode = LENGTH;
                        }
                    } catch (ParametersParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }

        return style;
    }

    public static Matrix createBitmapMatrix(Background background) {
        Matrix matrix = new Matrix();
        matrix.setTranslate(background.getX(), background.getY());
        return matrix;
    }

    public int getColorWidthMode() {
        return colorWidthMode;
    }

    public int getColorHeightMode() {
        return colorHeightMode;
    }

    public float getColorWidth() {
        return colorWidth;
    }

    public float getColorHeight() {
        return colorHeight;
    }

    private static String repeatToString(int repeat) {
        switch (repeat) {
            case REPEAT:
                return "repeat";
            case REPEAT_X:
                return "repeat-x";
            case REPEAT_Y:
                return "repeat-y";
            case NO_REPEAT:
            default:
                return "no-repeat";

        }
    }

    boolean isAndroidResource() {
        return isAndroidResource;
    }
}
