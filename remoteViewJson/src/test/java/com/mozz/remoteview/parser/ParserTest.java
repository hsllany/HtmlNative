package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.reader.StringCodeReader;

import org.junit.Test;

public class ParserTest {

    static {
        Parser.toggleDebug(true);
    }

    @Test
    public void process() throws Exception {
        parserDebugger(CodesForTest.CodeSimple);
    }

    private void parserDebugger(String code) {

        System.out.println("code is \n" + code);

        StringCodeReader reader = new StringCodeReader(code);
        Parser parser = new Parser(reader);

        try {
            RVContext rootTree = parser.process();
            System.out.println("\ntree is :");
            System.out.println(rootTree.mRootTree.wholeTreeToString());

            System.out.println("\nfunction is :");
            System.out.println(rootTree.mFunctionTable.toString());

        } catch (SyntaxError sytaxError) {
            sytaxError.printStackTrace();
        }

    }

}