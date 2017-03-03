package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        sReservedTagClassTable.put("flexbox", "com.google.android.flexbox.FlexboxLayout");
        sReservedTagClassTable.put("scroller", "android.widget.ScrollView");
        sReservedTagClassTable.put("box", "android.widget.AbsoluteLayout");
    }

    /**
     * Looking for related class name via tag. ViewRegistry will first look in
     * {@link ViewRegistry#sReservedTagClassTable}, if not found, will continuously look in
     * {@link ViewRegistry#sExtraTagClassTable}
     *
     * @param tag tag name found in .layout file
     * @return corresponding class name, or null if not found
     */
    @Nullable
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

    @Nullable
    static Attr findAttrFromExtraByTag(String tag) {
        if (sExtraTagClassTable == null)
            sExtraTagClassTable = new ArrayMap<>();

        return sExtraTagClassTable.get(tag);
    }

    static void registerExtraView(String tag, @NonNull RView rView) {
        if (sExtraTagClassTable == null)
            sExtraTagClassTable = new ArrayMap<>();

        sExtraTagClassTable.put(tag, rView);
    }
}
