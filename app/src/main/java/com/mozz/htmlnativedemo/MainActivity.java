package com.mozz.htmlnativedemo;

import android.content.Intent;
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

import com.mozz.htmlnative.HNEnvironment;
import com.mozz.htmlnative.HNHead;
import com.mozz.htmlnative.HNative;

import static com.mozz.htmlnativedemo.WebViewActivity.EXTAL_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mSearch;
    ImageButton mGo;

    ViewGroup mContainer;

    RemoteViewLoader mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContainer = (RelativeLayout) findViewById(R.id.relative_view);
        mSearch = (EditText) findViewById(R.id.search_editbox);
        mGo = (ImageButton) findViewById(R.id.search_go_btn);
        mGo.setOnClickListener(this);
        mLoader = new RemoteViewLoader(this);
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
                Toast.makeText(this, HNEnvironment.v, Toast.LENGTH_SHORT).show();
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
            mLoader.load(url, new HNative.OnHNViewLoaded() {
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
        } else {
            Toast.makeText(this, "not valid url", Toast.LENGTH_LONG).show();
        }
    }
}
