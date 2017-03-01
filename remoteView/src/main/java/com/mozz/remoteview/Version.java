package com.mozz.remoteview;

/**
 * @author Yang Tao, 17/3/1.
 */

public final class Version {

    private Version() {
    }

    public static final int versionCode = 1;
    public static final String versionName = "v0.1.0";

    public static final String v = "RemoteView-" + Version.versionName + "(" +
            Version.versionCode + ")";
}
