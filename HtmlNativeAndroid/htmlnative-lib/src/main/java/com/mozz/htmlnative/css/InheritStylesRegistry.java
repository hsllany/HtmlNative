package com.mozz.htmlnative.css;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Yang Tao, 17/4/1.
 */

public final class InheritStylesRegistry {

    private InheritStylesRegistry() {

    }

    /**
     * To save the inherit style
     */
    private static final Set<String> sInheritStyles = new HashSet<>();

    /**
     * To save the preserved style that dose not inherit
     */
    private static final Set<String> sPreservedStyles = new HashSet<>();

    public static void register(String style) {
        sInheritStyles.add(style);
    }

    public static void preserve(String style) {
        sPreservedStyles.add(style);
    }

    public static boolean isInherit(String style) {
        return sInheritStyles.contains(style);
    }

    public static boolean isPreserved(String style) {
        return sPreservedStyles.contains(style);
    }

    public static Iterator<String> iterator() {
        return sInheritStyles.iterator();
    }
}
