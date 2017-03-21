package com.mozz.htmlnative.script;

import com.mozz.htmlnative.HNSandBoxContext;

/**
 * @author Yang Tao, 17/3/21.
 */

public abstract class ScriptRunner {
    protected final HNSandBoxContext mSandbox;

    public ScriptRunner(HNSandBoxContext sandBoxContext) {
        this.mSandbox = sandBoxContext;
    }

    public abstract void run(String script);
}
