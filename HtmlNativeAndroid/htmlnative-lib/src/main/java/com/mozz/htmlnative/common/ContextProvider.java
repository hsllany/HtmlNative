package com.mozz.htmlnative.common;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @author Yang Tao, 17/5/26.
 */

public final class ContextProvider {
    private static WeakReference<Context> mApplicationRef;

    public static void install(@NonNull Application application) {
        mApplicationRef = new WeakReference<Context>(application);
    }

    @Nullable
    public static Context getApplicationRef() {
        return mApplicationRef.get();
    }
}
