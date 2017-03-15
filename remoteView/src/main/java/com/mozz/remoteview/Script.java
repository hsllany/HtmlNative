package com.mozz.remoteview;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;


public final class Script {
    private static final String TAG = Script.class.getSimpleName();
    public static final boolean DEBUG = RVEnvironment.DEBUG;
    private String mCode;
    private String mFunctionName;

    private Script(String code, String functionName) {
        mCode = code;
        mFunctionName = functionName;
    }

    @NonNull
    public static Script toCode(String function, String code) {
        return new Script(code, function);
    }

    @Override
    public String toString() {
        return mCode;
    }

    public void execute(@NonNull RVSandBoxContext context) {
        long timeStart = SystemClock.currentThreadTimeMillis();

        context.execute(mCode);

        long spend = SystemClock.currentThreadTimeMillis() - timeStart;

        if (DEBUG) {
            Log.d(TAG, "=====" + mFunctionName + "=====");
            Log.d(TAG, mCode);
            Log.i(TAG, "executed " + mFunctionName + " spend " + spend + "ms.");
            Log.d(TAG, "=====");
        }
    }
}
