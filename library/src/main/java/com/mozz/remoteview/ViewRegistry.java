package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
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
        sReservedTagClassTable.put(HtmlTag.P, TextView.class.getName());
        sReservedTagClassTable.put(HtmlTag.IMG, ImageView.class.getName());
        sReservedTagClassTable.put(HtmlTag.INPUT, EditText.class.getName());
        sReservedTagClassTable.put(HtmlTag.BUTTON, Button.class.getName());
        sReservedTagClassTable.put("linearbox", LinearLayout.class.getName());
        sReservedTagClassTable.put("flexbox", FlexboxLayout.class.getName());
        sReservedTagClassTable.put(HtmlTag.SCROLLER, ScrollView.class.getName());
        sReservedTagClassTable.put("box", AbsoluteLayout.class.getName());
        sReservedTagClassTable.put(RVDomTree.INNER_TREE_TAG, TextView.class.getName());
        sReservedTagClassTable.put(HtmlTag.IFRAME, WebView.class.getName());
        sReservedTagClassTable.put(HtmlTag.WEB, WebView.class.getName());
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
