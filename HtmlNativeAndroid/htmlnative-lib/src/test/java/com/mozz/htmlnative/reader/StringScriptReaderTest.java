package com.mozz.htmlnative.reader;

import org.junit.Test;

import java.io.EOFException;

import static com.mozz.htmlnative.CodeToTest.codeScriptFirst;

/**
 * Created by Yang Tao on 17/2/21.
 */
public class StringScriptReaderTest {

    private static final String TAG = "RV_StringCodeReaderTest";


    @Test
    public void testStringReader() throws Exception {
        StringTextReader reader = new StringTextReader(codeScriptFirst);

        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (true) {
            try {
                char c = reader.nextCh();
                System.out.println("-->" + c + "," + (int) c);
                sb.append(c);
                assert c == codeScriptFirst.charAt(i);
                i++;
            } catch (EOFException e) {
                System.out.println(sb.toString());
                break;
            }
        }

        assert sb.toString().equals(codeScriptFirst);
    }

}