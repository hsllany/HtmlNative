package com.mozz.htmlnative;


/**
 * @author Yang Tao, 17/3/1.
 */

public final class HNEnvironment {

    public static boolean DEBUG = false;

    private HNEnvironment() {
    }

    public static final int versionCode = 1;
    public static final String versionName = "v0.1.0";

    public static final String v = "RemoteView-" + HNEnvironment.versionName + "(" +
            HNEnvironment.versionCode + ")";

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }
}
