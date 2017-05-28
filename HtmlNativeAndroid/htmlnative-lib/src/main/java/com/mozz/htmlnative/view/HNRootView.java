package com.mozz.htmlnative.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/8.
 */

public final class HNRootView extends ScrollView {

    private final Map<String, View> mViewWithId = new ArrayMap<>();

    private static final String TAG = HNRootView.class.getSimpleName();

    private FrameLayout mContentView;

    public HNRootView(@NonNull Context context) {
        super(context);
        mContentView = new FrameLayout(context);
        super.addView(mContentView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    public void addContent(View v, ViewGroup.LayoutParams layoutParams) {
        mContentView.addView(v, layoutParams);
    }

    public View findViewById(@NonNull String id) {
        return mViewWithId.get(id);
    }


    public View putViewWithId(String id, View view) {
        View before = mViewWithId.put(id, view);
        if (before != null) {
            Log.w(TAG, "Duplicated id " + id + ", before is " + before + ", current is " + view);
        }
        return before;
    }

    public String allIdTag() {
        return mViewWithId.toString();
    }

    public boolean containsView(String id) {
        return mViewWithId.containsKey(id);
    }


}
