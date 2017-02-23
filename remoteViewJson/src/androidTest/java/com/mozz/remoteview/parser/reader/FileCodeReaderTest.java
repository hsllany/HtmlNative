package com.mozz.remoteview.parser.reader;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.mozz.remoteview.TestActivity;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.EOFException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class FileCodeReaderTest {

    private static final String TAG = FileCodeReaderTest.class.getSimpleName();

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            TestActivity.class);

    private Activity mActivity;

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void nextCh() throws Exception {
        InputStream inputStream = mActivity.getAssets().open("example.luav");
        String code = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();

        FileCodeReader reader = new FileCodeReader(mActivity.getAssets().open("example.luav"));

        Log.d(TAG, "load file" + code);
        int i = 0;

        while (true) {
            try {
                char c = reader.nextCh();
                Log.d(TAG, "--->" + c);
                assertTrue(c == code.charAt(i++));

            } catch (EOFException e) {
                break;
            }
        }
    }

    @Test
    public void line() throws Exception {

    }

    @Test
    public void current() throws Exception {

    }

}