package com.mozz.remoteview;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mozz.remoteview.parser.Parser;
import com.mozz.remoteview.parser.RemoteViewInflater;
import com.mozz.remoteview.parser.SyntaxTree;
import com.mozz.remoteview.parser.SyntaxError;
import com.mozz.remoteview.parser.reader.StringCodeReader;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        long time2 = SystemClock.uptimeMillis();
//        View v2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_test, null);
//        Log.d("TestDemo", "LayoutInflater========>spend " + (SystemClock.uptimeMillis() - time2));

        RemoteViewLoader loader = new RemoteViewLoader(this, "http://192.168.31.52/testLayout.xml");
        loader.load(new OnViewLoaded() {
            @Override
            public void onViewLoaded(View v) {
                setContentView(v);
            }
        });
    }
}
