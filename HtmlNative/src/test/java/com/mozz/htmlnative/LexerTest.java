package com.mozz.htmlnative;

import com.mozz.htmlnative.reader.StringTextReader;
import com.mozz.htmlnative.token.Token;

import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;

public class LexerTest {

    private static final String TAG = "RV_LexerTest";

    private static final String testCode = "<body>\n" +
            "    <iframe a=123.4e5/>\n" +
            "</body>";

    private static final String testScriptCode = "<script> hello world; 1 < 2; /n</script>";

    @Test
    public void testLexerSimple() throws Exception {
        LexerDebugger(testCode);
    }

    private void LexerDebugger(String code) throws IOException, HNSyntaxError {

        debug("code:");
        debug(code + "\n\ntoken list is:\n");

        Lexer lexer = new Lexer(new StringTextReader(code));

        while (true) {
            try {
                Token t = lexer.scan();
                debug("---> " + t.toString());
            } catch (EOFException e) {
                break;
            } catch (HNSyntaxError sytaxError) {
                sytaxError.printStackTrace();
                break;

            }
        }
    }

    private void debug(String msg) {
        System.out.println(msg);
    }

    @Test
    public void testScript() throws EOFException, HNSyntaxError {
        Lexer lexer = new Lexer(new StringTextReader(testScriptCode));

        try {
            Token t01 = lexer.scan();
            Token t02 = lexer.scan();
            Token t03 = lexer.scan();
            Token t = lexer.scanScript();
            Token t1 = lexer.scan();
            Token t2 = lexer.scan();
            Token t3 = lexer.scan();
            Token t4 = lexer.scan();


            System.out.println(t01.toString());
            System.out.println(t02.toString());
            System.out.println(t03.toString());
            System.out.println(t.toString());
            System.out.println(t1.toString());
            System.out.println(t2.toString());
            System.out.println(t3.toString());
            System.out.println(t4.toString());
        }catch (EOFException e){

        }

    }

}