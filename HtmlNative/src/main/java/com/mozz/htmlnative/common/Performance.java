package com.mozz.htmlnative.common;

/**
 * @author Yang Tao, 17/3/1.
 */

public final class Performance {

    static final String TAG = PerformanceWatcher.class.getSimpleName();

    public static boolean OPEN = true;

    private Performance() {
    }

    public static PerformanceWatcher newWatcher() {
        return newWatcher(!OPEN);
    }

    private static PerformanceWatcher newWatcher(boolean ignored) {
        if (ignored) {
            return EmptyPerformanceWatcher.instance;
        }

        return new PerformanceWatcherImpl();
    }
}
