package com.mozz.htmlnative;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.mozz.htmlnative.script.ScriptRunner;

import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/2/24.
 */
final class HNScriptRunnerThread {

    @NonNull
    private static HandlerThread mScriptThread = new HandlerThread("HNScriptRunner");
    private static Handler mHandler;
    private static OnScriptCallback mErrorCallback;

    public static void init() {
        mScriptThread.start();
        mHandler = new Handler(mScriptThread.getLooper());

    }

    public static void quit() {
        mScriptThread.quit();
        mErrorCallback = null;
    }

    public static void runScript(HNSandBoxContext context, ScriptRunner runner, String script) {
        post(new ScriptRunTask(context, runner, script));
    }

    public static void post(Runnable r) {
        mHandler.post(noException(r));
    }

    public static void postAtFront(Runnable r) {
        mHandler.postAtFrontOfQueue(noException(r));
    }

    public static void setErrorCallback(OnScriptCallback mErrorCallback) {
        HNScriptRunnerThread.mErrorCallback = mErrorCallback;
    }

    private static final class ExceptionCatcherRunnable implements Runnable {

        private Runnable mWrappedRunnable;

        ExceptionCatcherRunnable(Runnable wrappedRunnable) {
            mWrappedRunnable = wrappedRunnable;
        }

        @Override
        public void run() {
            try {
                mWrappedRunnable.run();
            } catch (Throwable e) {
                if (mErrorCallback != null) {
                    mErrorCallback.error(e);
                }
            }
        }


    }

    private static ExceptionCatcherRunnable noException(Runnable runnable) {
        return new ExceptionCatcherRunnable(runnable);
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
