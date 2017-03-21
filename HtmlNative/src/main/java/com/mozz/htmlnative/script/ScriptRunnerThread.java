package com.mozz.htmlnative.script;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.mozz.htmlnative.HNSandBoxContext;

import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/24.
 */
public final class ScriptRunnerThread {

    @NonNull
    private HandlerThread mScriptThread = new HandlerThread("HNScriptRunner");
    private Handler mHandler;

    private ScriptRunnerThread() {
        mScriptThread.start();
        mHandler = new Handler(mScriptThread.getLooper());
    }

    public void quit() {
        mScriptThread.quit();
    }

    public void runScript(HNSandBoxContext context, ScriptRunner runner, String script) {
        mHandler.post(new ScriptRunTask(context, runner, script));
    }

    private static ScriptRunnerThread instance = null;

    @NonNull
    public static ScriptRunnerThread getInstance() {
        if (instance == null) {
            synchronized (ScriptRunnerThread.class) {
                if (instance == null) {
                    instance = new ScriptRunnerThread();
                }
            }
        }

        return instance;
    }

    private static class ScriptRunTask implements Runnable {

        WeakReference<HNSandBoxContext> mContextRef;
        WeakReference<ScriptRunner> mRunnerRef;
        String script;

        ScriptRunTask(HNSandBoxContext context, ScriptRunner runner, String script) {

            mContextRef = new WeakReference<>(context);
            mRunnerRef = new WeakReference<>(runner);
            this.script = script;
        }

        @Override
        public void run() {
            ScriptRunner runner = mRunnerRef.get();
            HNSandBoxContext context = mContextRef.get();

            if (runner != null && context != null && script != null) {
                runner.run(this.script);
            }
        }
    }
}
