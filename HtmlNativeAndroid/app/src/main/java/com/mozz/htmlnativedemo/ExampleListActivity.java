package com.mozz.htmlnativedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang Tao, 17/3/13.
 */

public class ExampleListActivity extends AppCompatActivity implements AdapterView
        .OnItemClickListener {

    private Object[] mAssetFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mAssetFileList = filterHtmlFile(AssetsUtils.allFiles(this)).toArray();
            ListView listView = new ListView(this);
            ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<>(this, R.layout
                    .example_list_item, R.id.example_list_title, mAssetFileList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(this);
            setContentView(listView);
        } catch (IOException e) {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAssetFileList != null && mAssetFileList.length >= position + 1) {
            Intent i = new Intent(this, LayoutExampleActivity.class);
            i.putExtra(LayoutExampleActivity.EXTRA_KEY_RV_FILE, mAssetFileList[position].toString
                    ());

            startActivity(i);
        }
    }

    private static List<String> filterHtmlFile(String[] files) {
        List<String> stringList = new ArrayList<>();
        for (String s : files) {
            if (s.endsWith(".html") || s.endsWith(".layout")) {
                stringList.add(s);
            }
        }

        return stringList;
    }
}
