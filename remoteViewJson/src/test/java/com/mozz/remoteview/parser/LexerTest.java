package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.reader.StringCodeReader;
import com.mozz.remoteview.parser.token.Token;

import org.junit.Test;

import java.io.IOException;

public class LexerTest {

    @Test
    public void testLexerSimple() {
        LexerDebugger(CodesForTest.CodeComplicated);
    }

    private void LexerDebugger(String code) {

        System.out.println("code:");
        System.out.println(code + "\n\ntoken list is:\n");

        Lexer lexer = new Lexer(new StringCodeReader(code));

        while (true) {
            try {
                Token t = lexer.scan();
                System.out.println("---> " + t.toString());
            } catch (IOException e) {
                break;
            } catch (SyntaxError sytaxError) {
                sytaxError.printStackTrace();
            }


        }
    }

}