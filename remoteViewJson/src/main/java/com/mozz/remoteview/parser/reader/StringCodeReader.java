package com.mozz.remoteview.parser.reader;

import com.mozz.remoteview.parser.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringCodeReader extends StreamReader {

    private InputStream inputStream;

    public StringCodeReader(String str) {
        super();
        str = str + ' ';
        inputStream = new ByteArrayInputStream(str.getBytes());
        setInputStream(new InputStreamReader(inputStream));
    }

    @Override
    public void close() {
        Utils.closeQuitely(inputStream);
        super.close();
    }
}
