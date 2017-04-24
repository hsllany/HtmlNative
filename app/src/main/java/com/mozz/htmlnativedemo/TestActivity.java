package com.mozz.htmlnativedemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import com.mozz.htmlnative.view.HtmlLayout;

/**
 * @author Yang Tao, 17/4/18.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup body = new HtmlLayout(this);

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ViewGroup div1 = new HtmlLayout(this);
        ViewGroup div2 = new HtmlLayout(this);

        div1.setBackgroundColor(Color.GREEN);
        div2.setBackgroundColor(Color.BLUE);
        body.addView(div1, layoutParams);
        body.addView(div2, layoutParams);

        setContentView(body);
    }
}
