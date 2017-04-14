package com.mozz.htmlnativedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import static com.mozz.htmlnativedemo.LayoutExampleActivity.EXTRA_KEY_RV_FILE;

/**
 * @author Yang Tao, 17/4/14.
 */

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);
        mWebview = (WebView) findViewById(R.id.webview);
        String fileName = getIntent().getStringExtra(EXTRA_KEY_RV_FILE);
        mWebview.loadUrl("file:///android_asset/" + fileName);
    }
}
