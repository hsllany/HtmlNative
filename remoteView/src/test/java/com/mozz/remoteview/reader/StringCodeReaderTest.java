package com.mozz.remoteview.reader;

import com.mozz.remoteview.parser.CodeToTest;

import org.junit.Test;

import java.io.EOFException;

/**
 * Created by Yang Tao on 17/2/21.
 */
public class StringCodeReaderTest {

    private static final String TAG = "RV_StringCodeReaderTest";


    @Test
    public void testStringReader() throws Exception {
        StringCodeReader reader = new StringCodeReader(CodeToTest.codeScriptFirst);

        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (true) {
            try {
                char c = reader.nextCh();
                System.out.println("-->" + c + "," + (int) c);
                sb.append(c);
//                assertTrue(c == codeScriptFirst.charAt(i));
//                System.out.println("->" + c + " , at " + i);

                i++;
            } catch (EOFException e) {
                System.out.println(sb.toString());
                break;
//                Log.d(TAG, e.getMessage());

            }
        }

//        assertTrue(i == codeScriptFirst.length());
    }

}