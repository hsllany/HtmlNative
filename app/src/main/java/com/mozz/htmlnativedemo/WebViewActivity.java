package com.mozz.htmlnativedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * @author Yang Tao, 17/4/14.
 */

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebview;

    static final String EXTAL_URL = "webview_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);
        mWebview = (WebView) findViewById(R.id.webview);
        String url = getIntent().getStringExtra(EXTAL_URL);
        mWebview.loadUrl(url);
    }
}
