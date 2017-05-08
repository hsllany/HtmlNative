package com.mozz.htmlnative.css;

import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.mozz.htmlnative.utils.ParametersUtils;

/**
 * @author Yang Tao, 17/3/24.
 */

public class Background {

    public static final int REPEAT = 0x00000001;
    public static final int REPEAT_X = 0x00000002;
    public static final int REPEAT_Y = 0x00000003;
    public static final int NO_REPEAT = 0x00000004;

    public static final int POSITION_MODE_LENGTH = 0x00000001;
    public static final int POSITION_MODE_PERCENTAGE = 0x00000002;

    private static final int LK_COLOR = 0;
    private static final int LK_URL = 1;
    private static final int LK_REPEAT = 2;
    private static final int LK_X = 3;
    private static final int LK_Y = 4;


    private String url = "";
    private int color = Color.TRANSPARENT;
    private boolean colorSet = false;
    private int repeat = REPEAT;
    private float x;
    private float y;
    private int width;
    private int height;

    private int widthMode = POSITION_MODE_PERCENTAGE;
    private int heightMode = POSITION_MODE_PERCENTAGE;


    @Override
    public String toString() {
        return "background:" + color + " url(" + url + ") repeat=" + repeat + " x=" + x + " y=" + y;
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

    public static Background createOrChange(String param, String val, Object oldOne) {
        Background style;
        if (oldOne == null) {
            style = new Background();
        } else {
            style = (Background) oldOne;
        }

        String[] subStrings = val.trim().split("\\s+");

        switch (param) {
            case "background": {
                int lk = LK_COLOR;
                for (String item : subStrings) {

                    if (item.startsWith("url(") && lk <= LK_URL) {
                        style.setUrl(item.substring(item.indexOf('(') + 1, item.lastIndexOf(')'))
                                .trim());
                        lk = LK_URL + 1;

                    } else if (item.startsWith("#") && lk == LK_COLOR) {
                        try {
                            style.setColor(ParametersUtils.toColor(item));
                        } catch (IllegalArgumentException ignored) {

                        }

                        lk = LK_COLOR + 1;

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

                    } else if (lk == LK_X) {
                        try {
                            if (item.endsWith("%")) {
                                style.setX(ParametersUtils.getPercent(item));
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("left")) {
                                style.setX(0f);
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("right")) {
                                style.setX(1.f);
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setX(.5f);
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                            } else {
                                style.setX((float) ParametersUtils.toPixel(item).getPxValue());
                                style.widthMode = POSITION_MODE_LENGTH;
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }

                        lk++;

                    } else if (lk == LK_Y) {
                        try {
                            if (item.endsWith("%")) {
                                style.setY(ParametersUtils.getPercent(item));
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("top")) {
                                style.setY(0f);
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("bottom")) {
                                style.setY(1.f);
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setY(.5f);
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                            } else {
                                style.setY((float) ParametersUtils.toPixel(item).getPxValue());
                                style.heightMode = POSITION_MODE_LENGTH;
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }

                        lk++;

                    } else if (lk == LK_COLOR) {
                        try {
                            style.setColor(ParametersUtils.toColor(item));
                        } catch (IllegalArgumentException e) {
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
                        style.setColor(ParametersUtils.toColor(subStrings[0]));
                    } catch (IllegalArgumentException ignored) {
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
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                                style.setX(ParametersUtils.getPercent(item));
                            } else if (item.equals("left")) {
                                style.setX(0f);
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("right")) {
                                style.setX(1.f);
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setX(.5f);
                                style.widthMode = POSITION_MODE_PERCENTAGE;
                            } else {
                                style.widthMode = POSITION_MODE_LENGTH;
                                style.setX((float) ParametersUtils.toPixel(item).getPxValue());
                            }
                        } else if (lookFor == LK_Y) {
                            if (item.endsWith("%")) {
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                                style.setY(ParametersUtils.getPercent(item));
                            } else if (item.equals("top")) {
                                style.setY(0f);
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("bottom")) {
                                style.setY(1.f);
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                            } else if (item.equals("center")) {
                                style.setY(.5f);
                                style.heightMode = POSITION_MODE_PERCENTAGE;
                            } else {
                                style.heightMode = POSITION_MODE_LENGTH;
                                style.setY((float) ParametersUtils.toPixel(item).getPxValue());
                            }
                        } else {
                            break;
                        }
                    } catch (IllegalArgumentException e) {
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


                }
            }
            break;
        }


        Log.d("StyleBackground", style.toString());
        return style;
    }

    public static Matrix createBitmapMatrix(Background background) {
        Matrix matrix = new Matrix();
        matrix.setTranslate(background.getX(), background.getY());
        return matrix;
    }
}
