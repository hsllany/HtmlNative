package com.mozz.htmlnative.parser;

/**
 * @author Yang Tao, 17/7/15.
 */

final class ParseHelper {
    private ParseHelper() {
    }

    static String[] parseMimeType(String mimeTypeString) {
        if (mimeTypeString == null) {
            return null;
        }

        mimeTypeString = mimeTypeString.trim();

        String[] mimePair = new String[2];

        int slashIndex = mimeTypeString.indexOf('/');
        if (slashIndex > 0) {
            mimePair[0] = mimeTypeString.substring(0, slashIndex);
            mimePair[1] = mimeTypeString.substring(slashIndex + 1, mimeTypeString.length());
        } else {
            mimePair[0] = mimeTypeString;
        }

        return mimePair;
    }

}
