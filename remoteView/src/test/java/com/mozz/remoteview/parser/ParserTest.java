package com.mozz.remoteview.parser;

import android.util.Log;

import com.mozz.remoteview.parser.reader.StringCodeReader;

import org.junit.Test;

import static com.mozz.remoteview.parser.CodeToTest.codeScriptFirst;
import static com.mozz.remoteview.parser.CodeToTest.codeScriptOnly;
import static com.mozz.remoteview.parser.CodeToTest.codeTemplateFirst;
import static com.mozz.remoteview.parser.CodeToTest.codeTemplateOnly;

public class ParserTest {

    private static String TAG = "RV_ParseTest";

    static {
        Parser.toggleDebug(true);
    }

    @Test
    public void process() throws Exception {
        debug("============codeScriptFirst=============");
        parserDebugger(codeScriptFirst);

        debug("============codeScriptOnly==============");
        parserDebugger(codeScriptOnly);

        debug("============codeTemplateFirst==============");
        parserDebugger(codeTemplateFirst);

        debug("=============codeTemplateOnly==============");
        parserDebugger(codeTemplateOnly);
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
            debug(rootTree.mFunctionTable.toString());

        } catch (RVSyntaxError sytaxError) {
            sytaxError.printStackTrace();
            throw sytaxError;
        }

    }

    private void debug(String msg) {
        System.out.println(msg);
    }

}