package com.mozz.remoteview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mozz.remoteview.reader.StringCodeReader;

import org.junit.Test;

import static com.mozz.remoteview.CodeToTest.codeScriptFirst;
import static com.mozz.remoteview.CodeToTest.codeScriptOnly;
import static com.mozz.remoteview.CodeToTest.codeTemplateFirst;
import static com.mozz.remoteview.CodeToTest.codeTemplateOnly;

public class ParserTest {

    private static String TAG = "RV_ParseTest";

    private static String code = "<body>\n" +
            "\t<p>hello worldnihao</p>\n" +
            "\t<img src=\"http://www.baidu.com\"/></body>";

    static {
        Parser.toggleDebug(true);
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

        StringCodeReader reader = new StringCodeReader(code);
        Parser parser = new Parser(reader);

        try {
            RVModule rootTree = parser.process();
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

        RVModule rvModule = new RVModule();
        RVDomTree tree = new RVDomTree(rvModule, null, 0, 0);

        Parser.parseStyle(tree, style);

        System.out.println(rvModule.mAttrs.toString());
    }

}