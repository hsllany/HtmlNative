package com.mozz.htmlnative;


/**
 * @author Yang Tao, 17/3/1.
 */

public final class HNEnvironment {

    private HNEnvironment() {
    }

    public static final int versionCode = 1;
    public static final String versionName = "v0.1.0";

    public static final String PERFORMANCE_TAG = "HNPerformanceRecord";

    public static final String v = "HtmlNative " + HNEnvironment.versionName + "(" +
            HNEnvironment.versionCode + ")";
}
