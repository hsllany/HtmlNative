package com.mozz.remoteview.parser;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author YangTao7
 */

public final class AttrsSet {

    private static final String TAG = AttrsSet.class.getSimpleName();
    private static boolean DEBUG = false;

    private static final String ATTR_WIDTH = "width";
    private static final String ATTR_HEIGHT = "height";
    private static final String ATTR_BACKGROUND = "background";
    private static final String ATTR_PADDING = "padding";
    private static final String ATTR_PADDING_LEFT = "paddingLeft";
    private static final String ATTR_PADDING_RIGHT = "paddingRight";
    private static final String ATTR_PADDING_TOP = "paddingTop";
    private static final String ATTR_PADDING_BOTTOM = "paddingBottom";
    private static final String ATTR_ALPHA = "alpha";

    private Map<String, Object> mAttrs;

    private static Map<Class<? extends View>, Attr> sCachedAttrs = new HashMap<>();

    public AttrsSet() {
        mAttrs = new ArrayMap<>(6);
    }

    public void put(String paramsKey, String value) {
        mAttrs.put(paramsKey, value);
    }

    public void put(String paramsKey, double value) {
        mAttrs.put(paramsKey, value);
    }

    public void put(String paramsKey, int value) {
        mAttrs.put(paramsKey, value);
    }

    @Override
    public String toString() {
        return mAttrs.toString();
    }

    public void apply(Context context, View v, ViewGroup.LayoutParams layoutParams) {
        Iterator<Map.Entry<String, Object>> itr = mAttrs.entrySet().iterator();

        if (v instanceof LinearLayout)
            ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);


        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        while (itr.hasNext()) {
            Map.Entry<String, Object> entry = itr.next();

            String params = entry.getKey();
            Object value = entry.getValue();

            if (params.equals("text") && v instanceof TextView) {
                ((TextView) v).setText(value.toString());

            } else if (params.equals(ATTR_WIDTH)) {
                if (value instanceof Integer) {
                    width = (Integer) entry.getValue();
                } else if (value.toString().equalsIgnoreCase("MATCH_PARENT")) {
                    width = ViewGroup.LayoutParams.MATCH_PARENT;
                }

            } else if (params.equals(ATTR_HEIGHT)) {
                if (value instanceof Integer) {
                    height = (Integer) entry.getValue();
                } else if (value.toString().equalsIgnoreCase("MATCH_PARENT")) {
                    height = ViewGroup.LayoutParams.MATCH_PARENT;
                }

            } else if (params.equals(ATTR_BACKGROUND)) {
                try {
                    int backgroundColor = Color.parseColor(value.toString());
                    v.setBackgroundColor(backgroundColor);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            } else if (params.equals(ATTR_PADDING)) {
                if (value instanceof Integer) {
                    int padding = (int) value;

                    v.setPadding(padding, padding, padding, padding);
                }

            } else if (params.equals(ATTR_PADDING_LEFT)) {
                if (value instanceof Integer) {
                    v.setPadding((int) value, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
                }

            } else if (params.equals(ATTR_PADDING_RIGHT)) {
                if (value instanceof Integer) {
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), (int) value, v.getPaddingBottom());
                }

            } else if (params.equals(ATTR_PADDING_TOP)) {
                if (value instanceof Integer) {
                    v.setPadding(v.getPaddingLeft(), (int) value, v.getPaddingRight(), v.getPaddingBottom());
                }

            } else if (params.equals(ATTR_PADDING_BOTTOM)) {
                if (value instanceof Integer) {
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), (int) value);
                }

            } else if (params.equals(ATTR_ALPHA)) {
                if (value instanceof Double) {
                    double d = (double) value;
                    v.setAlpha((float) d);
                }

            } else {
                Attr attr = sCachedAttrs.get(v.getClass());
                if (attr == null) {
                    attr = getAttrFromView(v.getClass());
                    if (attr != null) {
                        sCachedAttrs.put(v.getClass(), attr);
                    }
                }

                if (attr != null) {
                    attr.apply(context, v, params, value);
                }
            }


        }

        layoutParams.height = height;
        layoutParams.width = width;
    }

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }

    private static Attr getAttrFromView(Class<? extends View> clazz) {
        if (clazz.equals(TextView.class)) {
            return new TextViewAttr();
        } else if (clazz.equals(ImageView.class)) {
            return new ImageViewAttr();
        } else {
            return null;
        }
    }

}
