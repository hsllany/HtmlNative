package com.mozz.htmlnative;


/**
 * @author Yang Tao, 17/3/1.
 */

public final class RVEnvironment {

    public static final String STD_TAG = "RVPrint";

    public static boolean DEBUG = false;

    private RVEnvironment() {
    }

    public static final int versionCode = 1;
    public static final String versionName = "v0.1.0";

    public static final String v = "RemoteView-" + RVEnvironment.versionName + "(" +
            RVEnvironment.versionCode + ")";

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }
}
