package com.mozz.htmlnativedemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozz.htmlnative.view.HNDiv;

/**
 * @author Yang Tao, 17/4/18.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HNDiv divView = new HNDiv(this);
        divView.setPadding(10, 20, 30, 0);

        {
            HNDiv.HNDivLayoutParams params = new HNDiv.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.positionMode = HNDiv.HNDivLayoutParams.POSITION_STATIC;
            divView.addView(generateView("hello world", Color.GREEN));
        }

        {
            HNDiv.HNDivLayoutParams params = new HNDiv.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDiv.HNDivLayoutParams.POSITION_FLOAT_LEFT;
            divView.addView(generateView("left", Color.RED), params);
        }

        {
            HNDiv.HNDivLayoutParams params = new HNDiv.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDiv.HNDivLayoutParams.POSITION_FLOAT_LEFT;
            params.setMargins(10, 20, 0, 0);
            divView.addView(generateView("left2", Color.BLUE), params);
        }

        {
            HNDiv.HNDivLayoutParams params = new HNDiv.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDiv.HNDivLayoutParams.POSITION_FLOAT_RIGHT;
            params.setMargins(10, 20, 0, 0);
            divView.addView(generateView("right right right", Color.CYAN), params);
        }

        {
            HNDiv.HNDivLayoutParams params = new HNDiv.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDiv.HNDivLayoutParams.POSITION_FLOAT_RIGHT;
            params.setMargins(10, 20, 0, 0);
            divView.addView(generateView("right right rightright right rightright right " +
                    "rightright right rightright right rightright right rightright right " +
                    "rightright right rightright right rightright right rightright right " +
                    "rightright right rightright right right", Color.DKGRAY), params);
        }

        {
            HNDiv.HNDivLayoutParams params = new HNDiv.HNDivLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.positionMode = HNDiv.HNDivLayoutParams.POSITION_FLOAT_LEFT;
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
