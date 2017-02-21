package com.mozz.remoteview.json.parser;

import com.mozz.remoteview.json.parser.reader.StringCodeReader;
import com.mozz.remoteview.json.parser.token.Token;

import org.junit.Test;

import java.io.IOException;

public class LexerTest {

    @Test
    public void testLexerSimple() {
        LexerDebugger("<template>\n  <text class=-12.3e-5></text><image class=\"video_box_subtitle\"></image></template>");
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
            } catch (SytaxError sytaxError) {
                sytaxError.printStackTrace();
            }


        }
    }

}