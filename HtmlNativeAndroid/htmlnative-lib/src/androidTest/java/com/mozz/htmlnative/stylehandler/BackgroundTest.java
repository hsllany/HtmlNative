package com.mozz.htmlnative.stylehandler;

import android.graphics.Color;

import com.mozz.htmlnative.css.Background;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Yang Tao, 17/5/2.
 */
public class BackgroundTest {
    @Test
    public void create() throws Exception {
        String s1 = "red";
        Background r1 = Background.createOrChange("background-color", s1, null);
        Assert.assertTrue(r1.getColor() == Color.RED);

        String s2 = "url(http://n.sinaimg.cn/news/crawl/20170302/18ey-fycaahm6004808.jpg)";
        Background r2 = Background.createOrChange("background-image", s2, null);
        Assert.assertTrue(r2.getUrl().equals("http://n.sinaimg" +
                ".cn/news/crawl/20170302/18ey-fycaahm6004808.jpg"));

        String s3 = "left center";
        Background r3 = Background.createOrChange("background-position", s3, null);
        Assert.assertTrue(r3.getX() == 0.f && r3.getY() == 0.5f);

        String s4 = "repeat-x";
        Background r4 = Background.createOrChange("background-repeat", s4, null);
        Assert.assertTrue(r4.getRepeat() == Background.REPEAT_X);

        String s5 = "red url(http://www.baidu.com)  repeat-x left center";
        Background r5 = Background.createOrChange("background", s5, null);
        Assert.assertTrue(r5.getRepeat() == Background.REPEAT_X);
        Assert.assertTrue(r5.getX() == 0.f);
        Assert.assertTrue(r5.getY() == 0.5f);
        Assert.assertTrue(r5.getColor() == Color.RED);
        Assert.assertTrue(r5.getUrl().equals("http://www.baidu.com"));
    }

}