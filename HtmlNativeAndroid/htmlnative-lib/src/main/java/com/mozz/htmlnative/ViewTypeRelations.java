package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.htmlnative.view.HNDiv;
import com.mozz.htmlnative.view.HNImg;
import com.mozz.htmlnative.view.HNText;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/3.
 */

public final class ViewTypeRelations {

    static final String BOX = "box";
    static final String LINEAR_BOX = "linear_box";
    static final String FLEX_BOX = "flex_box";

    /**
     * Relate the tag and class.
     */
    private static final Map<String, String> sReservedTagClassTable = new ArrayMap<>();

    /**
     * For extra tag, lazy initialize later.
     */
    private static Map<String, String> sExtraTagClassTable;

    static {
        sReservedTagClassTable.put(BOX, HNDiv.class.getName());
        sReservedTagClassTable.put(LINEAR_BOX, HNDiv.class.getName());
        sReservedTagClassTable.put(FLEX_BOX, FlexboxLayout.class.getName());

        sReservedTagClassTable.put(HtmlTag.BODY, HNDiv.class.getName());
        sReservedTagClassTable.put(HtmlTag.TEMPLATE, HNDiv.class.getName());

        sReservedTagClassTable.put(HtmlTag.P, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.TEXT, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.IMG, HNImg.class.getName());
        sReservedTagClassTable.put(HtmlTag.INPUT, EditText.class.getName());
        sReservedTagClassTable.put(HtmlTag.BUTTON, Button.class.getName());
        sReservedTagClassTable.put(HtmlTag.SCROLLER, ScrollView.class.getName());
        sReservedTagClassTable.put(HtmlTag.IFRAME, WebView.class.getName());
        sReservedTagClassTable.put(HtmlTag.WEB, WebView.class.getName());
        sReservedTagClassTable.put(HtmlTag.A, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.SPAN, HNDiv.class.getName());
        sReservedTagClassTable.put(HtmlTag.H1, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.H2, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.H3, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.H4, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.H5, HNText.class.getName());
        sReservedTagClassTable.put(HtmlTag.H6, HNText.class.getName());

        // for inner element only
        sReservedTagClassTable.put(HtmlTag.INNER_TREE_TAG, TextView.class.getName());
    }

    /**
     * Looking for related class name via tag. ViewTypeRelations will first look in
     * {@link ViewTypeRelations#sReservedTagClassTable}, if not found, will continuously look in
     * {@link ViewTypeRelations#sExtraTagClassTable}
     *
     * @param type tag name found in .layout file
     * @return corresponding class name, or null if not found
     */
    @Nullable
    public static String findClassByType(@NonNull String type) {
        String viewClassName = sReservedTagClassTable.get(type.toLowerCase());

        if (viewClassName != null) {
            return viewClassName;
        }

        if (sExtraTagClassTable == null) {
            return null;
        }

        viewClassName = sExtraTagClassTable.get(type);
        if (viewClassName != null) {
            return viewClassName;
        }

        return null;
    }

    static void registerExtraView(@NonNull String htmlType, @NonNull String androidViewClassName) {
        if (sExtraTagClassTable == null) {
            sExtraTagClassTable = new ArrayMap<>();
        }

        sExtraTagClassTable.put(htmlType, androidViewClassName);

    }

    static void unregisterExtraView(@NonNull String htmlType) {
        if (sExtraTagClassTable != null) {
            sExtraTagClassTable.remove(htmlType);
        }
    }

    static void clearAllExtraView() {
        if (sExtraTagClassTable != null) {
            sExtraTagClassTable.clear();
        }
    }
}
