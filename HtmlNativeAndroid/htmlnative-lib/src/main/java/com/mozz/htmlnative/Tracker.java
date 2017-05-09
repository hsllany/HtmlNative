package com.mozz.htmlnative;

import android.util.Log;

/**
 * @author Yang Tao, 17/5/9.
 */

public final class Tracker {
    private long[] timeSpend = new long[10];
    private String[] processes = new String[10];

    private int mIndex = 0;

    public Tracker() {

    }

    public synchronized void record(String process, long timeInterval) {
        if (mIndex < 10) {
            timeSpend[mIndex] = timeInterval;
            processes[mIndex] = process;
            mIndex++;
        } else {
            Log.e("Tracker", "Can't record more");
        }
    }

    public synchronized void reset() {
        mIndex = 0;
    }

    public synchronized String dump() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mIndex; i++) {
            sb.append(processes[i]).append(" : ").append(timeSpend[i]).append("ms ").append("\n");
        }

        return sb.toString();
    }
}
