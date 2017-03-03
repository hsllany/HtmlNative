package com.mozz.remoteview;

import android.util.ArrayMap;

import com.mozz.remoteview.attrs.Attr;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/3.
 */

final class ViewRegistry {

    /**
     * Relate the tag and class.
     */
    private static final Map<String, String> sReservedTagClassTable = new ArrayMap<>();

    /**
     * For extra tag, lazy initialize later.
     */
    private static Map<String, RView> sExtraTagClassTable;

    static {
        sReservedTagClassTable.put("text", "android.widget.TextView");
        sReservedTagClassTable.put("image", "android.widget.ImageView");
        sReservedTagClassTable.put("input", "android.widget.EditText");
        sReservedTagClassTable.put("button", "android.widget.Button");
        sReservedTagClassTable.put("linearbox", "android.widget.LinearLayout");
        sReservedTagClassTable.put("flexbox", "android.widget.LinearLayout");
        sReservedTagClassTable.put("scroller", "android.widget.ScrollView");
        sReservedTagClassTable.put("box", "android.widget.AbsoluteLayout");
    }

    static String findClassByTag(String tag) {
        String viewClassName = sReservedTagClassTable.get(tag);

        if (viewClassName != null)
            return viewClassName;

        if (sExtraTagClassTable == null)
            return null;

        RView rView = sExtraTagClassTable.get(tag);
        if (rView != null)
            return rView.onGetViewClassName().getName();

        return null;
    }

    static Attr findAttrFromExtraByTag(String tag) {
        return sExtraTagClassTable.get(tag);
    }

    static void registerExtraView(String tag, RView rView) {
        if (sExtraTagClassTable == null)
            sExtraTagClassTable = new ArrayMap<>();

        sExtraTagClassTable.put(tag, rView);
    }
}
