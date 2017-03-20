package com.mozz.htmlnative.common;

import android.graphics.Color;

import junit.framework.Assert;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Yang Tao, 17/3/13.
 */
public class UtilsTest {
    @Test
    public void closeQuitely() throws Exception {
        Closeable closeable = null;
        Utils.closeQuitely(closeable);

        closeable = new Closeable() {
            @Override
            public void close() throws IOException {

            }
        };

        Utils.closeQuitely(closeable);
    }

    @Test
    public void color() throws Exception {
        String color1 = "#ff0000";
        Assert.assertTrue(Utils.color(color1) == Color.RED);

        String color2 = "#f00";
        Assert.assertTrue(Utils.color(color2) == Color.RED);

        String color3 = "#ffff0000";
        Assert.assertTrue(Utils.color(color3) == Color.RED);
    }

    @Test
    public void toInt() throws Exception {

    }

    @Test
    public void toFloat() throws Exception {

    }

    @Test
    public void px() throws Exception {

    }

    @Test
    public void toBoolean() throws Exception {

    }

}