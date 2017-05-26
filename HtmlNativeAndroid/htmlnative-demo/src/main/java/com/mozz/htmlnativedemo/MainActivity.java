package com.mozz.htmlnativedemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mozz.htmlnative.HNativeEngine;
import com.mozz.htmlnative.dom.HNHead;

import static com.mozz.htmlnativedemo.WebViewActivity.EXTAL_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mSearch;
    ImageButton mGo;
    ViewGroup mContainer;
    RemoteViewLoader mLoader;

    SharedPreferences mSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSharedPreference = getSharedPreferences("htmlnative-demo", MODE_PRIVATE);
        String url = mSharedPreference.getString("url", "");

        mContainer = (RelativeLayout) findViewById(R.id.relative_view);
        mSearch = (EditText) findViewById(R.id.search_editbox);
        mSearch.setText(url);
        mGo = (ImageButton) findViewById(R.id.search_go_btn);
        mGo.setOnClickListener(this);
        mLoader = new RemoteViewLoader(this);
        mSearch.clearFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.layout_example:
                Intent i = new Intent(this, ExampleListActivity.class);
                startActivity(i);
                break;

            case R.id.about:
                Intent ii = new Intent(this, TestActivity.class);
                startActivity(ii);
                break;

            case R.id.main_in_webview:
                String url = mSearch.getText().toString();
                if (URLUtil.isValidUrl(url)) {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra(EXTAL_URL, url);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "not valid url", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String url = mSearch.getText().toString();

        if (URLUtil.isValidUrl(url)) {
            mLoader.load(url, new HNativeEngine.OnHNViewLoaded() {
                @Override
                public void onViewLoaded(View v) {
                    mContainer.removeAllViews();
                    mContainer.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                            .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onHead(HNHead head) {

                }
            });

            SharedPreferences.Editor editor = mSharedPreference.edit();
            editor.putString("url", url);
            editor.apply();
        } else {
            Toast.makeText(this, "not valid url", Toast.LENGTH_LONG).show();
        }
    }
}
