package com.mozz.htmlnative.utils;

import android.graphics.Color;
import android.util.TypedValue;

import com.mozz.htmlnative.common.PixelValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertTrue;

/**
 * @author Yang Tao, 17/3/13.
 */
@RunWith(JUnit4.class)
public class ParametersUtilsTest {
    @Before
    public void setUp() {
        ParametersUtils.init(4, 4);
    }

    @Test
    public void color() throws Exception {

        String color1 = "#ff0000";
        assertTrue(ParametersUtils.toColorSafe(color1) == Color.RED);

        String color2 = "#f00";
        assertTrue(ParametersUtils.toColorSafe(color2) == Color.RED);

        String color3 = "#ffff0000";
        assertTrue(ParametersUtils.toColorSafe(color3) == Color.RED);
    }

    @Test
    public void toInt() throws Exception {
        String i1 = "123";
        assertTrue(ParametersUtils.toInt(i1) == 123);

        int i2 = 123;
        assertTrue(ParametersUtils.toInt(i2) == 123);

    }

    @Test
    public void toFloat() throws Exception {
        String i1 = "123.3f";
        assertTrue(Float.compare(ParametersUtils.toFloat(i1), 123.3f) == 0);

        float i2 = 123.3f;
        assertTrue(Float.compare(ParametersUtils.toFloat(i2), 123.3f) == 0);
    }

    @Test
    public void px() throws Exception {

    }

    @Test
    public void toBoolean() throws Exception {

    }

    @Test
    public void toPixel() throws Exception {
        String a = "23px";
        PixelValue p = ParametersUtils.toPixelSafe(a);

        assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_PX);
        assertTrue(p.getPxValue() == 23);

        a = "123.5dp";
        p = ParametersUtils.toPixelSafe(a);

        assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_DIP);
        assertTrue(p.getPxValue() == ParametersUtils.dpToPx(123.5f));

        a = "123.5";
        p = ParametersUtils.toPixelSafe(a);

        assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_PX);
        assertTrue(p.getPxValue() == 123.5);

        a = "@hello world";
        p = ParametersUtils.toPixelSafe(a);

        assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_PX);
        assertTrue(p.getPxValue() == 0);

        a = "@";
        p = ParametersUtils.toPixelSafe(a);

        assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_PX);
        assertTrue(p.getPxValue() == 0);

        a = "asdkangk";
        p = ParametersUtils.toPixelSafe(a);

        assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_PX);
        assertTrue(p.getPxValue() == 0);
    }
}