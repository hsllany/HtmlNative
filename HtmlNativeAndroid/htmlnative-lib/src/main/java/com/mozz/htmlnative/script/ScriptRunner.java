package com.mozz.htmlnative.script;

import com.mozz.htmlnative.HNSandBoxContext;

/**
 * @author Yang Tao, 17/3/21.
 */

public abstract class ScriptRunner {
    protected final HNSandBoxContext mSandbox;

    private OnScriptCallback mErrorCallback;

    private static OnScriptCallback sUniversalCallback = null;

    public ScriptRunner(HNSandBoxContext sandBoxContext) {
        this.mSandbox = sandBoxContext;
    }

    public static void registerUniversalCallback(OnScriptCallback sUniversalCallback) {
        ScriptRunner.sUniversalCallback = sUniversalCallback;
    }

    public static void clearUniversalCallback() {
        ScriptRunner.sUniversalCallback = null;
    }

    public static OnScriptCallback getUniversalCallback() {
        return sUniversalCallback;
    }

    public abstract void run(String script);

    public abstract void runFunction(String functionName);

    public OnScriptCallback getScriptCallback() {
        return mErrorCallback;
    }

    public void registerScriptCallback(OnScriptCallback mErrorCallback) {
        this.mErrorCallback = mErrorCallback;
    }

    public interface OnScriptCallback {
        void error(Exception e);
    }

    protected final void dispatchScriptError(Exception e) {
        if (mErrorCallback != null) {
            mErrorCallback.error(e);
        }
    }

    public final void postRun(Runnable runnable) {
        mSandbox.postInScriptThread(runnable);
    }

}
