package com.mozz.htmlnative;

import android.graphics.Color;
import android.support.test.runner.AndroidJUnit4;

import com.mozz.htmlnative.attrs.BackgroundStyle;
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
        Object b = Styles.parseStyleSingle("background", background1);
        Assert.assertTrue(b instanceof BackgroundStyle);

        BackgroundStyle backgroundStyle = (BackgroundStyle) b;
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
            debug(rootTree.mRootTree.wholeTreeToString());

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


    @Test
    public void parseStyleTest() {
        String style = "a:  1;b:2";

        HNSegment HNSegment = new HNSegment();
        HNDomTree tree = new HNDomTree(HNSegment, null, 0, 0);

        Parser.parseStyle(tree, style);

        System.out.println(HNSegment.mAttrs.toString());
    }

}