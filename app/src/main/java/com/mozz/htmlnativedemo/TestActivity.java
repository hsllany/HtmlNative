package com.mozz.htmlnativedemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Yang Tao, 17/4/18.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIVView divView = new DIVView(this);
        setContentView(divView);

    }

    private static class DIVView extends View {

        public DIVView(Context context) {
            super(context);
        }

        public DIVView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DIVView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public DIVView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Resources res = getResources();
            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.a_webp);

            Matrix matrix = new Matrix();
            //            matrix.postTranslate(20, 100);
            matrix.setTranslate(500, 500);

            Bitmap newBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                    matrix, true);


            if(newBitmap.equals(bmp)) {
                canvas.drawColor(Color.RED);
            }
//            canvas.drawBitmap(newBitmap, 0, 0, null);
            canvas.drawBitmap(newBitmap, new Matrix(), null);


        }
    }
}
