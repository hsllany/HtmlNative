package com.mozz.remoteview.parser;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class ReservedWords {

    private static final String[] sWords = {"layout_width", "layout_height", "text"};

    public static boolean isReserved(String keyWords) {
        for (String word : sWords) {
            if (word.equals(keyWords))
                return true;
        }

        return false;
    }
}
