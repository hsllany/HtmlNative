package com.mozz.remoteview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Yang Tao, 17/3/2.
 */

public class LayoutExampleActivity extends AppCompatActivity {

    static final String EXTRA_KEY_RV_FILE = "rv_asset_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AssetsViewLoader mLoader = new AssetsViewLoader(this);
        String fileName = getIntent().getStringExtra(EXTRA_KEY_RV_FILE);
        mLoader.load(fileName);
    }
}
