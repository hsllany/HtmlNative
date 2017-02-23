package com.mozz.remoteview.parser.code;

import android.os.SystemClock;
import android.util.Log;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Created by Yang Tao on 17/2/23.
 */

public class Code {
    private static final String TAG = Code.class.getSimpleName();
    static final boolean DEBUG = false;
    private String mCode;
    private String mFunctionName;

    private Code(String code, String functionName) {
        mCode = code;
        mFunctionName = functionName;
    }

    public static Code toCode(String function, String code) {
        return new Code(code, function);
    }

    @Override
    public String toString() {
        return mCode;
    }

    public void execute() {
        long timeStart = SystemClock.currentThreadTimeMillis();

        LuaValue chunk = JsePlatform.standardGlobals().load(mCode);
        chunk.call();

        long spend = SystemClock.currentThreadTimeMillis() - timeStart;

        if (DEBUG) {
            Log.d(TAG, "=====" + mFunctionName + "=====");
            Log.d(TAG, mCode);
            Log.d(TAG, "=====");
        }

        Log.i(TAG, "executed " + mFunctionName + " spend " + spend + "ms.");


    }
}
