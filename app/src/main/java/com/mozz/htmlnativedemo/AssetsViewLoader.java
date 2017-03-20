package com.mozz.htmlnativedemo;

import android.app.Activity;
import android.widget.Toast;

import com.mozz.htmlnative.HNative;

import java.io.IOException;

/**
 * @author Yang Tao, 17/3/13.
 */

public class AssetsViewLoader {
    private Activity mActivity;

    public AssetsViewLoader(Activity activity) {
        mActivity = activity;
    }

    public void load(final String fileName) {
        try {
            HNative.getInstance().loadView(mActivity, mActivity.getAssets().open(fileName), mActivity);
        } catch (IOException e) {
            Toast.makeText(mActivity, "load file failed", Toast.LENGTH_SHORT).show();
        }
    }
}
