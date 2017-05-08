package com.mozz.htmlnativedemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mozz.htmlnative.HNativeEngine;
import com.mozz.htmlnative.dom.HNHead;

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
            HNativeEngine.getInstance().loadView(mActivity, mActivity.getAssets().open(fileName), new
                    HNativeEngine.OnHNViewLoaded() {

                @Override
                public void onViewLoaded(View v) {
                    if (mActivity != null && !mActivity.isDestroyed()) {
                        mActivity.setContentView(v);
                    }
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onHead(HNHead head) {
                    if (mActivity.getActionBar() != null) {
                        mActivity.getActionBar().setTitle(head.getTitle());
                    } else if (mActivity instanceof AppCompatActivity) {
                        if (((AppCompatActivity) mActivity).getSupportActionBar() != null) {
                            ((AppCompatActivity) mActivity).getSupportActionBar().setTitle(head
                                    .getTitle());
                        }
                    }
                }
            });
        } catch (IOException e) {
            Toast.makeText(mActivity, "load file failed", Toast.LENGTH_SHORT).show();
        }
    }
}
