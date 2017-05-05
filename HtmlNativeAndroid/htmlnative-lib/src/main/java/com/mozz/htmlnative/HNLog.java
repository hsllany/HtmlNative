package com.mozz.htmlnative;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author Yang Tao, 17/3/14.
 */

public final class HNLog {

    public static final int RENDER = 0;
    public static final int ATTR = 1;
    public static final int SANDBOX = 2;
    public static final int PROCESS_THREAD = 3;
    public static final int LEXER = 4;
    public static final int PARSER = 5;
    public static final int CSS_PARSER = 6;
    public static final int DOM = 7;
    public static final int STYLE = 8;

    @Retention(SOURCE)
    @IntDef({RENDER, ATTR, SANDBOX, PROCESS_THREAD, LEXER, PARSER, CSS_PARSER, DOM, STYLE})
    @interface EventType {
    }


    private static final String[] TAG_NAME = {"Renderer", "AttrsSet", "Sandbox", "ProcessThread",
            "Lexer", "Parser", "CssParser", "Dom", "Style"};

    private static int sDebugLevel = 0;

    private HNLog() {
    }

    public static void d(@EventType int tag, String msg) {
        if (((1 << tag) & sDebugLevel) != 0) {
            Log.d(TAG_NAME[tag], msg);
        }
    }

    public static void e(@EventType int tag, String msg) {
        Log.e(TAG_NAME[tag], msg);
    }

    public static void i(@EventType int tag, String msg) {
        Log.i(TAG_NAME[tag], msg);
    }

    public static void w(@EventType int tag, String msg) {
        Log.w(TAG_NAME[tag], msg);
    }

    public static void wtf(@EventType int tag, String msg) {
        Log.wtf(TAG_NAME[tag], msg);
    }

    public static void setDebugLevel(@EventType int tag) {
        sDebugLevel |= (1 << tag);
    }

}
