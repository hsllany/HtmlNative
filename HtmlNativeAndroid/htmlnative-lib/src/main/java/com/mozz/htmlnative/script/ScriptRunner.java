package com.mozz.htmlnative.script;

import com.mozz.htmlnative.HNSandBoxContext;

/**
 * @author Yang Tao, 17/3/21.
 */


public abstract class ScriptRunner {

    private HNSandBoxContext mSandbox;

    public final void attach(HNSandBoxContext context) {
        this.mSandbox = context;
    }

    public final HNSandBoxContext getSandboxContext() {
        return mSandbox;
    }

    public abstract void run(String script);

    public abstract void runFunction(String functionName);

    public final void postRun(Runnable runnable) {
        mSandbox.postInScriptThread(runnable);
    }

    public abstract void onLoad();

    public abstract void onUnload();
}
