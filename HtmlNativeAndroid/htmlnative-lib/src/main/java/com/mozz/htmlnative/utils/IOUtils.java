package com.mozz.htmlnative.utils;

import android.support.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * @author Yang Tao, 17/5/8.
 */

public class IOUtils {

    private IOUtils() {

    }

    public static void closeQuietly(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
                // do nothing
            }
        }
    }

    public static String postParamsToString(Map<String, String> postParams) {
        if (postParams == null || postParams.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : postParams.entrySet()) {
            if (!first) {
                sb.append("&");
            } else {
                first = false;
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
}
