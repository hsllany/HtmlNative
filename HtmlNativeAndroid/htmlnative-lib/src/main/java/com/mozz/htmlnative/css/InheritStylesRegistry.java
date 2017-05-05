package com.mozz.htmlnative.css;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang Tao, 17/4/1.
 */

public final class InheritStylesRegistry {

    private InheritStylesRegistry() {

    }

    private static final Set<String> sInheritAttrs = new HashSet<>();

    public static void register(String attr) {
        sInheritAttrs.add(attr);
    }

    public static boolean isInherit(String attr) {
        return sInheritAttrs.contains(attr);
    }
}
