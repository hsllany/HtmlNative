package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.reader.StringCodeReader;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Yang Tao on 17/2/21.
 */
public class StringCodeReaderTest {

    @Test
    public void testStringReader() {
        String codeTest = "hello world";
        StringCodeReader reader = new StringCodeReader(codeTest);

        int i = 0;
        while (true) {
            try {
                char c = reader.nextCh();
                assertTrue(c == codeTest.charAt(i));
                System.out.print(c + ",");

                i++;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        assertTrue(i == codeTest.length());
    }

}