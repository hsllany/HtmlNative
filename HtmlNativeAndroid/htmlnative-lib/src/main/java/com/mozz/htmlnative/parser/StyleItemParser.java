package com.mozz.htmlnative.parser;

import com.mozz.htmlnative.css.Background;
import com.mozz.htmlnative.css.Styles;

/**
 * @author Yang Tao, 17/5/10.
 */
public final class StyleItemParser {

    private static final CssParser.StyleHolder STYLE_HOLDER = new CssParser.StyleHolder();

    /**
     * to parse single style string into {@link CssParser.StyleHolder}. For example, 'background:url
     * (http://www.abc.com/efg.jpg)' will become :<br/>
     * StyleHolder.key = background<br/>
     * StyleHolder.obj = {@link Background}<br/>
     *
     * @param styleName,      raw style name
     * @param styleValue,     raw style string
     * @param oldStyleObject, old style object, if you have one; or null.
     * @return StyleHolder
     */
    public static CssParser.StyleHolder parseStyleSingle(String styleName, String styleValue,
                                                         Object oldStyleObject) {
        STYLE_HOLDER.key = null;
        STYLE_HOLDER.obj = null;

        if (styleName.startsWith(Styles.ATTR_BACKGROUND) || styleName.startsWith(Styles
                .ATTR_HN_BACKGROUND)) {
            Object val = Background.createOrChange(styleName, styleValue, oldStyleObject);
            STYLE_HOLDER.key = Styles.ATTR_BACKGROUND;
            STYLE_HOLDER.obj = val;
            return STYLE_HOLDER;
        } else {
            STYLE_HOLDER.key = styleName;
            STYLE_HOLDER.obj = styleValue.trim();
            return STYLE_HOLDER;
        }
    }

    public static String parseKey(String key) {
        if (key.startsWith(Styles.ATTR_BACKGROUND) || key.startsWith(Styles.ATTR_HN_BACKGROUND)) {
            return Styles.ATTR_BACKGROUND;
        } else {
            return key;
        }
    }
}
