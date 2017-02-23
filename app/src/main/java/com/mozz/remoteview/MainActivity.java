package com.mozz.remoteview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        long time2 = SystemClock.uptimeMillis();
//        View v2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_test, null);
//        Log.d("TestDemo", "LayoutInflater========>spend " + (SystemClock.uptimeMillis() - time2));

        RemoteViewLoader loader = new RemoteViewLoader(this, "http://10.58.107.21/testLayout.xml");
        loader.load(new OnViewLoaded() {
            @Override
            public void onViewLoaded(View v) {
                setContentView(v);
            }
        });
    }
}
