package com.mozz.remoteview.common;

import android.os.SystemClock;
import android.util.Log;

/**
 * @author Yang Tao, 17/3/1.
 */


public interface PerformanceWatcher {
    void check(String tag);

    void checkDone(String tag);
}

final class PerformanceWatcherImpl implements PerformanceWatcher {
    private final long time;
    private long lastTime = -1;
    private StringBuilder mSb;

    PerformanceWatcherImpl() {
        time = SystemClock.currentThreadTimeMillis();
        mSb = new StringBuilder();
    }

    @Override
    public void check(String tag) {

        if (lastTime == -1)
            lastTime = SystemClock.currentThreadTimeMillis();

        String currentMethod = Thread.currentThread().getStackTrace()[3].toString();


        long now = SystemClock.currentThreadTimeMillis();
        long duration = now - time;
        long durationLast = now - lastTime;
        mSb.append("[").append(duration).append(", ").append(durationLast).append("] ms : ")
                .append(tag).append(", @").append(currentMethod).append("\n");
        lastTime = now;
    }

    private void toLogcat() {
        Log.d(Performance.TAG, mSb.toString());
    }

    @Override
    public void checkDone(String tag) {
        if (lastTime == -1)
            lastTime = SystemClock.currentThreadTimeMillis();

        String currentMethod = Thread.currentThread().getStackTrace()[3].toString();


        long now = SystemClock.currentThreadTimeMillis();
        long duration = now - time;
        long durationLast = now - lastTime;
        mSb.append("[").append(duration).append(", ").append(durationLast).append("] ms : ")
                .append(tag).append(", @").append(currentMethod).append("\n");
        lastTime = now;

        toLogcat();
    }
}

final class EmptyPerformanceWatcher implements PerformanceWatcher {

    @Override
    public void check(String tag) {

    }

    @Override
    public void checkDone(String tag) {

    }

    static PerformanceWatcher instance = new EmptyPerformanceWatcher();
}
