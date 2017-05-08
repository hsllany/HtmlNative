package com.mozz.htmlnative.parser;

import android.graphics.Color;
import android.support.test.runner.AndroidJUnit4;

import com.mozz.htmlnative.HNSegment;
import com.mozz.htmlnative.TestGlobal;
import com.mozz.htmlnative.css.Background;
import com.mozz.htmlnative.exception.HNSyntaxError;
import com.mozz.htmlnative.parser.CssParser;
import com.mozz.htmlnative.parser.Parser;
import com.mozz.htmlnative.reader.StringTextReader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ParserTest {
    @Test
    public void parseStyleSingle() throws Exception {
        testBackground("url(http://n.sinaimg.cn/news/crawl/20170302/18ey-fycaahm6004808.jpg) #fff", "http://baidu.com", Color.WHITE);
        testBackground("url(http://baidu.com) white", "http://baidu.com", Color.WHITE);
        testBackground("#fff url(http://baidu.com) ", "http://baidu.com", Color.WHITE);

    }

    private void testBackground(String background, String url, int color) {
        String background1 = background;
        CssParser.StyleHolder b = CssParser.StyleItemParser.parseStyleSingle("background", background1, null);
        Assert.assertTrue(b.obj instanceof Background);

        Background backgroundStyle = (Background) b.obj;
        TestGlobal.toLog(b.toString());

        Assert.assertTrue(backgroundStyle.getUrl().equals(url));
        Assert.assertTrue(backgroundStyle.getColor() == color);
    }


    private static String code = "<body>\n" +
            "\t<p>hello worldnihao</p>\n" +
            "\t<img src=\"http://www.baidu.com\"/></body>";

    static {
    }

    @Test
    public void process() throws Exception {
        //        debug("============codeScriptFirst=============");
        //        parserDebugger(codeScriptFirst);
        //
        //        debug("============codeScriptOnly==============");
        //        parserDebugger(codeScriptOnly);
        //
        //        debug("============codeTemplateFirst==============");
        //        parserDebugger(codeTemplateFirst);
        //
        //        debug("=============codeTemplateOnly==============");
        parserDebugger(code);
    }

    private void parserDebugger(String code) throws HNSyntaxError {

        debug("code is \n" + code);

        StringTextReader reader = new StringTextReader(code);
        Parser parser = new Parser(reader);

        try {
            HNSegment rootTree = parser.process();
            debug("\ntree is :");
            debug(rootTree.mDom.wholeTreeToString());

            debug("\nfunction is :");
            debug(rootTree.toString());

        } catch (HNSyntaxError sytaxError) {
            sytaxError.printStackTrace();
            throw sytaxError;
        }

    }

    private void debug(String msg) {
        System.out.println(msg);
    }

}