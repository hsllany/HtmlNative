package com.mozz.htmlnativedemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozz.htmlnative.view.HNDivLayout;

/**
 * @author Yang Tao, 17/4/18.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HNDivLayout divView = new HNDivLayout(this);
        divView.setPadding(10, 20, 30, 0);

        {
            HNDivLayout.HNDivLayoutParams params = new HNDivLayout.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.positionMode = HNDivLayout.HNDivLayoutParams.POSITION_STATIC;
            divView.addView(generateView("hello world", Color.GREEN));
        }

        {
            HNDivLayout.HNDivLayoutParams params = new HNDivLayout.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDivLayout.HNDivLayoutParams.POSITION_FLOAT_LEFT;
            divView.addView(generateView("left", Color.RED), params);
        }

        {
            HNDivLayout.HNDivLayoutParams params = new HNDivLayout.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDivLayout.HNDivLayoutParams.POSITION_FLOAT_LEFT;
            params.setMargins(10, 20, 0, 0);
            divView.addView(generateView("left2", Color.BLUE), params);
        }

        {
            HNDivLayout.HNDivLayoutParams params = new HNDivLayout.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDivLayout.HNDivLayoutParams.POSITION_FLOAT_RIGHT;
            params.setMargins(10, 20, 0, 0);
            divView.addView(generateView("right right right", Color.CYAN), params);
        }

        {
            HNDivLayout.HNDivLayoutParams params = new HNDivLayout.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDivLayout.HNDivLayoutParams.POSITION_FLOAT_RIGHT;
            params.setMargins(10, 20, 0, 0);
            divView.addView(generateView("right right rightright right rightright right " +
                    "rightright right rightright right rightright right rightright right " +
                    "rightright right rightright right rightright right rightright right " +
                    "rightright right rightright right right", Color.DKGRAY), params);
        }

        {
            HNDivLayout.HNDivLayoutParams params = new HNDivLayout.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDivLayout.HNDivLayoutParams.POSITION_FLOAT_LEFT;
            params.setMargins(10, 20, 0, 0);
            divView.addView(generateView("left2 left2 left2 left2 left2 left2 left2 left2 left2 "
                    + "left2 left2 left2 left2 left2 left2 left2 left2 left2", Color.YELLOW),
                    params);
        }


        setContentView(divView);

    }

    private View generateView(String t, int color) {
        TextView v = new TextView(this);
        v.setText(t);

        v.setBackgroundColor(color);
        return v;
    }
}
