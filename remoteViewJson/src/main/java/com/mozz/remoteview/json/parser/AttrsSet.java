package com.mozz.remoteview.json.parser;

import android.graphics.Color;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;

/**
 * @author YangTao7
 */

public final class AttrsSet {
    private Map<String, Object> mAttrs;

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

    public void apply(View v, ViewGroup.LayoutParams layoutParams) {
        Iterator<Map.Entry<String, Object>> itr = mAttrs.entrySet().iterator();


        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        while (itr.hasNext()) {
            Map.Entry<String, Object> entry = itr.next();

            String params = entry.getKey();
            if (params.equals("text") && v instanceof TextView) {
                ((TextView) v).setText(entry.getValue().toString());
            }

            if (params.equals("width")) {
                if (entry.getValue() instanceof Integer) {
                    width = (Integer) entry.getValue();
                } else if (entry.getValue().toString().equalsIgnoreCase("MATCH_PARENT")) {
                    width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                continue;
            }

            if (params.equals("height")) {
                if (entry.getValue() instanceof Integer) {
                    height = (Integer) entry.getValue();
                } else if (entry.getValue().toString().equalsIgnoreCase("MATCH_PARENT")) {
                    height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                continue;
            }

            if (params.equals("background")) {
                try {
                    int backgroundColor = Color.parseColor(entry.getValue().toString());
                    v.setBackgroundColor(backgroundColor);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }

        layoutParams.height = height;
        layoutParams.width = width;

    }
}
