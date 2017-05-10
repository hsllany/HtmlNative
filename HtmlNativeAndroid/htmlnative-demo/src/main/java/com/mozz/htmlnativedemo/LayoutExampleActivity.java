package com.mozz.htmlnativedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mozz.htmlnative.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import static com.mozz.htmlnativedemo.WebViewActivity.EXTAL_URL;

/**
 * @author Yang Tao, 17/3/2.
 */

public class LayoutExampleActivity extends AppCompatActivity {

    static final String EXTRA_KEY_RV_FILE = "rv_asset_file";
    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AssetsViewLoader mLoader = new AssetsViewLoader(this);
        mFileName = getIntent().getStringExtra(EXTRA_KEY_RV_FILE);
        mLoader.load(mFileName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout_example, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_code:
                showCode();
                break;

            case R.id.in_webview:
                showWebview();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCode() {
        String code;
        try {
            code = readCode(getIntent().getStringExtra(EXTRA_KEY_RV_FILE));
            Intent i = new Intent(this, SourceHtmlActivity.class);
            i.putExtra(SourceHtmlActivity.INTENT_SOURCE_CODE, code);
            startActivity(i);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    String readCode(String fileName) throws IOException {
        InputStream s = getAssets().open(fileName);
        byte[] buffer = new byte[s.available()];
        s.read(buffer);
        IOUtils.closeQuietly(s);

        return new String(buffer);
    }

    private void showWebview() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(EXTAL_URL, "file:///android_asset/" + mFileName);
        startActivity(intent);
    }
}
