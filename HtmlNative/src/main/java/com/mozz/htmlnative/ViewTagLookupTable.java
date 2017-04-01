package com.mozz.htmlnative;

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
import com.mozz.htmlnative.attrs.AttrHandler;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/3.
 */

public final class ViewTagLookupTable {

    /**
     * Relate the tag and class.
     */
    private static final Map<String, String> sReservedTagClassTable = new ArrayMap<>();

    /**
     * For extra tag, lazy initialize later.
     */
    private static Map<String, HNViewItem> sExtraTagClassTable;

    static {
        sReservedTagClassTable.put("box", AbsoluteLayout.class.getName());
        sReservedTagClassTable.put("linearbox", LinearLayout.class.getName());
        sReservedTagClassTable.put("flexbox", FlexboxLayout.class.getName());

        sReservedTagClassTable.put(HtmlTag.BODY, LinearLayout.class.getName());
        sReservedTagClassTable.put(HtmlTag.TEMPLATE, LinearLayout.class.getName());

        sReservedTagClassTable.put(HtmlTag.P, TextView.class.getName());
        sReservedTagClassTable.put(HtmlTag.TEXT, TextView.class.getName());
        sReservedTagClassTable.put(HtmlTag.IMG, ImageView.class.getName());
        sReservedTagClassTable.put(HtmlTag.INPUT, EditText.class.getName());
        sReservedTagClassTable.put(HtmlTag.BUTTON, Button.class.getName());
        sReservedTagClassTable.put(HtmlTag.SCROLLER, ScrollView.class.getName());
        sReservedTagClassTable.put(HtmlTag.IFRAME, WebView.class.getName());
        sReservedTagClassTable.put(HtmlTag.WEB, WebView.class.getName());
        sReservedTagClassTable.put(HtmlTag.A, TextView.class.getName());
        sReservedTagClassTable.put(HtmlTag.SPAN, TextView.class.getName());
        sReservedTagClassTable.put(HtmlTag.H1, TextView.class.getName());
        sReservedTagClassTable.put(HtmlTag.H2, TextView.class.getName());

        sReservedTagClassTable.put(HNDomTree.INNER_TREE_TAG, TextView.class.getName());
    }

    /**
     * Looking for related class name via tag. ViewTagLookupTable will first look in
     * {@link ViewTagLookupTable#sReservedTagClassTable}, if not found, will continuously look in
     * {@link ViewTagLookupTable#sExtraTagClassTable}
     *
     * @param tag tag name found in .layout file
     * @return corresponding class name, or null if not found
     */
    @Nullable
    public static String findClassByTag(@NonNull String tag) {
        String viewClassName = sReservedTagClassTable.get(tag.toLowerCase());

        if (viewClassName != null) {
            return viewClassName;
        }

        if (sExtraTagClassTable == null) {
            return null;
        }

        HNViewItem HNViewItem = sExtraTagClassTable.get(tag);
        if (HNViewItem != null) {
            return HNViewItem.onGetViewClassName().getName();
        }

        return null;
    }

    @Nullable
    public static AttrHandler findAttrFromExtraByTag(String tag) {
        if (sExtraTagClassTable == null) {
            sExtraTagClassTable = new ArrayMap<>();
        }

        return sExtraTagClassTable.get(tag);
    }

    public static void registerExtraView(String tag, @NonNull HNViewItem HNViewItem) {
        if (sExtraTagClassTable == null) {
            sExtraTagClassTable = new ArrayMap<>();
        }

        sExtraTagClassTable.put(tag, HNViewItem);
    }
}
