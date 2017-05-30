package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * @author Yang Tao, 17/2/24.
 */
public interface HNSandBoxContext extends VariablePool {
    /**
     * get Android Context
     *
     * @return context, see {@link android.content.Context}
     */
    Context getAndroidContext();

    /**
     * to execute script
     *
     * @param script to run
     */
    void execute(String script);

    void executeFun(String funName);

    /**
     * Looking for View by Id set in .layout file
     *
     * @param id {@link java.lang.String}
     * @return View {@link android.view.View} ,or null if not found
     */
    @Nullable
    View findViewById(@NonNull String id);

    boolean containsView(String id);

    void onViewLoaded();

    void onViewCreate();

    @Nullable
    View registerId(String id, View value);

    String allIdTag();

    HNSegment getSegment();

    void postInScriptThread(Runnable r);

}

