package com.mozz.htmlnative;

import android.support.annotation.NonNull;

import com.mozz.htmlnative.token.TokenType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang Tao, 17/3/6.
 */

public final class HtmlTag {
    private HtmlTag() {
    }

    public static final String A = "a";
    public static final String P = "p";
    public static final String H1 = "h1";
    public static final String H2 = "h2";
    public static final String H3 = "h3";
    public static final String H4 = "h4";
    public static final String B = "b";
    public static final String INPUT = "input";
    public static final String IMG = "img";
    public static final String DIV = "div";
    public static final String BUTTON = "button";
    public static final String SCROLLER = "scroller";
    public static final String IFRAME = "iframe";
    public static final String WEB = "web";
    public static final String BR = "br";
    public static final String SPAN = "span";
    public static final String BODY = "body";
    static final String TEMPLATE = TokenType.Template.toString();
    public static final String TEXT = "text";

    public static final String HEAD = "head";
    public static final String META = "meta";
    public static final String LINK = "link";

    /**
     * If parser met with swallowInnerTag, the inner element of token will become the
     * attribute of the element instead of creating a new child tree.
     */
    private static final Set<String> sSwallowInnerTag = new HashSet<>(7);

    static {
        sSwallowInnerTag.add(HtmlTag.A);
        sSwallowInnerTag.add(HtmlTag.B);
        sSwallowInnerTag.add(HtmlTag.H1);
        sSwallowInnerTag.add(HtmlTag.H2);
        sSwallowInnerTag.add(HtmlTag.H3);
        sSwallowInnerTag.add(HtmlTag.H4);
        sSwallowInnerTag.add(HtmlTag.INPUT);
        sSwallowInnerTag.add(HtmlTag.P);
        sSwallowInnerTag.add(HtmlTag.TEXT);
        sSwallowInnerTag.add(HtmlTag.BUTTON);
        sSwallowInnerTag.add(HtmlTag.SPAN);
    }

    ;

    static boolean isSwallowInnerTag(@NonNull String tag) {
        return sSwallowInnerTag.contains(tag.toLowerCase());
    }

    static boolean isDivOrTemplate(@NonNull String tag) {
        return tag.equalsIgnoreCase(HtmlTag.DIV) || tag.equalsIgnoreCase(HtmlTag.TEMPLATE);
    }

}
