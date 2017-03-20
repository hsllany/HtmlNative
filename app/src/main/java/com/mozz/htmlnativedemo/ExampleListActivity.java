package com.mozz.htmlnativedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Yang Tao, 17/3/13.
 */

public class ExampleListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String[] sList = {"webview.layout", "imageview.layout", "text.layout", "flex.layout", "body_para.layout"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.example_list_item, R.id.example_list_title, sList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(this);

        setContentView(listView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, LayoutExampleActivity.class);
        i.putExtra(LayoutExampleActivity.EXTRA_KEY_RV_FILE, sList[position]);
        startActivity(i);
    }
}
