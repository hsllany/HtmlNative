package com.mozz.htmlnative;

import com.mozz.htmlnative.reader.StringTextReader;

import org.junit.Test;

public class ParserTest {

    private static String TAG = "RV_ParseTest";

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

    private void parserDebugger(String code) throws RVSyntaxError {

        debug("code is \n" + code);

        StringTextReader reader = new StringTextReader(code);
        Parser parser = new Parser(reader);

        try {
            RVSegment rootTree = parser.process();
            debug("\ntree is :");
            debug(rootTree.mRootTree.wholeTreeToString());

            debug("\nfunction is :");
            debug(rootTree.toString());

        } catch (RVSyntaxError sytaxError) {
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

        RVSegment rvSegment = new RVSegment();
        RVDomTree tree = new RVDomTree(rvSegment, null, 0, 0);

        Parser.parseStyle(tree, style);

        System.out.println(rvSegment.mAttrs.toString());
    }

}