package com.mozz.htmlnative;

import com.mozz.htmlnative.exception.HNSyntaxError;
import com.mozz.htmlnative.parser.Lexer;
import com.mozz.htmlnative.reader.StringTextReader;
import com.mozz.htmlnative.token.Token;

import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;

public class LexerTest {
    
    private static final String testCode = "<body>\n" +
            "    <iframe a=123.4e5/>\n" +
            "</body>";

    private static final String testScriptCode = "<script> \nhello world; 1 < 2; /n</script>";
    private static final String testScriptCode2 = "<script></script>";
    private static final String testScriptCode3 = "<script> </script>";

    @Test
    public void testLexerSimple() throws Exception {
        LexerDebugger(testCode);
    }

    private static void LexerDebugger(String code) throws IOException, HNSyntaxError {

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

    private static void debug(String msg) {
        System.out.println(msg);
    }

    @Test
    public void testScript() throws HNSyntaxError {
        LexerScriptDebugger(testScriptCode);
        LexerScriptDebugger(testScriptCode2);
        LexerScriptDebugger(testScriptCode3);
    }

    public static void LexerScriptDebugger(String codeSample) throws HNSyntaxError {
        Lexer lexer = new Lexer(new StringTextReader(codeSample));

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

            System.out.println("----------");
        } catch (EOFException e) {

        }

    }

}