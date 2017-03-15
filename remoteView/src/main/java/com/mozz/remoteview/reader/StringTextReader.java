package com.mozz.remoteview.reader;

import com.mozz.remoteview.common.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringTextReader extends StreamReader {

    private InputStream inputStream;

    public StringTextReader(String str) {
        super();
        inputStream = new ByteArrayInputStream(str.getBytes());
        setInputStream(new InputStreamReader(inputStream));
    }

    @Override
    public void close() {
        Utils.closeQuitely(inputStream);
        super.close();
    }
}
