package com.mozz.htmlnative.common;

import android.graphics.Color;
import android.util.TypedValue;

import com.mozz.htmlnative.utils.ParametersUtils;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Yang Tao, 17/3/13.
 */
public class ParametersUtilsTest {

    @Test
    public void color() throws Exception {
        String color1 = "#ff0000";
        Assert.assertTrue(ParametersUtils.toColor(color1) == Color.RED);

        String color2 = "#f00";
        Assert.assertTrue(ParametersUtils.toColor(color2) == Color.RED);

        String color3 = "#ffff0000";
        Assert.assertTrue(ParametersUtils.toColor(color3) == Color.RED);
    }

    @Test
    public void toInt() throws Exception {
        String i1 = "123";
        Assert.assertTrue(ParametersUtils.toInt(i1) == 123);

        int i2 = 123;
        Assert.assertTrue(ParametersUtils.toInt(i2) == 123);

    }

    @Test
    public void toFloat() throws Exception {
        String i1 = "123.3f";
        Assert.assertTrue(Float.compare(ParametersUtils.toFloat(i1), 123.3f) == 0);

        float i2 = 123.3f;
        Assert.assertTrue(Float.compare(ParametersUtils.toFloat(i2), 123.3f) == 0);
    }

    @Test
    public void px() throws Exception {

    }

    @Test
    public void toBoolean() throws Exception {

    }

    @Test
    public void toPixel() throws Exception {
        String a = "1px";
        PixelValue p = ParametersUtils.toPixel(a);

        org.junit.Assert.assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_PX);
        org.junit.Assert.assertTrue(p.getValue() == 23);

        a = "123.5dp";
        p = ParametersUtils.toPixel(a);

        org.junit.Assert.assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_DIP);
        org.junit.Assert.assertTrue(p.getValue() == 123.5);

        a = "123.5";
        p = ParametersUtils.toPixel(a);

        org.junit.Assert.assertTrue(p.getUnit() == TypedValue.COMPLEX_UNIT_PX);
        org.junit.Assert.assertTrue(p.getValue() == 123.5);
    }

}