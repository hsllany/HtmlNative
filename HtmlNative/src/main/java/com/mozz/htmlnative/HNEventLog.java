package com.mozz.htmlnative;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author Yang Tao, 17/3/14.
 */

public final class HNEventLog {

    private static final String TAG = "RVEvent";

    public static final int TAG_RENDER = 0;
    public static final int TAG_PARSER = 1;
    public static final int TAG_ATTR = 2;
    public static final int TAG_VIEW_CONTEXT = 3;
    public static final int TAG_LEXER = 4;
    public static final int TAG_SCRIPT = 5;

    @Retention(SOURCE)
    @IntDef({TAG_RENDER, TAG_PARSER, TAG_ATTR, TAG_VIEW_CONTEXT, TAG_LEXER})
    @interface EventType {
    }

    private static final String[] TAG_NAME = {"RV_Render", "RV_Parser", "RV_AttrsSet",
            "RV_ViewContext", "RV_Lexer", "RV_Script"};

    private static int sDebugLevel = 0;

    private HNEventLog() {
    }

    public static void writeEvent(@EventType int tag, String msg) {
        if (((1 << tag) & sDebugLevel) != 0) {
            Log.i(TAG, "[" + TAG_NAME[tag] + "] " + msg);
        }
    }

    public static void writeError(@EventType int tag, String error) {
        if (((1 << tag) & sDebugLevel) != 0) {
            Log.e(TAG, "[" + TAG_NAME[tag] + "] " + error);
        }
    }

    public static void setDebugLevel(@EventType int tag) {
        sDebugLevel |= (1 << tag);
    }

}
