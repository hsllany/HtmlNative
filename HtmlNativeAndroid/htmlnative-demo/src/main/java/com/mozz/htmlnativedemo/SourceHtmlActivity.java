package com.mozz.htmlnativedemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author Yang Tao, 17/5/10.
 */

public class SourceHtmlActivity extends Activity {

    public static final String INTENT_SOURCE_CODE = "source";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String source = getIntent().getStringExtra(INTENT_SOURCE_CODE);

        TextView sourceCodeTxt = new TextView(this);
        sourceCodeTxt.setBackgroundColor(Color.parseColor("#363636"));
        sourceCodeTxt.setTextColor(Color.WHITE);
        if (source != null) {
            sourceCodeTxt.setText(source);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(sourceCodeTxt, new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(scrollView);
    }
}
