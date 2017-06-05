package com.mozz.htmlnative.script;

import com.mozz.htmlnative.HNSandBoxContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang Tao, 17/3/21.
 */

public abstract class ScriptRunner {
    private static Set<ScriptLib> sLibs = new HashSet<>();

    protected final HNSandBoxContext mSandbox;

    public ScriptRunner(HNSandBoxContext sandBoxContext) {
        this.mSandbox = sandBoxContext;
        installLibs();
    }

    public abstract void run(String script);

    public abstract void runFunction(String functionName);

    public final void postRun(Runnable runnable) {
        mSandbox.postInScriptThread(runnable);
    }

    private void installLibs() {
        for (ScriptLib lib : sLibs) {
            installLib(lib);
        }
    }

    protected abstract void installLib(ScriptLib lib);

    public static final void registerLib(ScriptLib lib) {
        sLibs.add(lib);
    }

}
