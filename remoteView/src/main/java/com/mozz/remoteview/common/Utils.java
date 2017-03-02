package com.mozz.remoteview.common;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Yang Tao on 17/2/24.
 */

public final class Utils {
    private Utils() {
    }

    public static void closeQuitely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
                // do nothing
            }
        }
    }

    public static boolean isSubClass(Class<?> subClass, Class<?> compareClass) {
        if (subClass.equals(compareClass))
            return true;

        else
            return false;
    }

}
