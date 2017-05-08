package com.mozz.htmlnative.reader;

import com.mozz.htmlnative.utils.IOUtils;

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
        IOUtils.closeQuietly(inputStream);
        super.close();
    }
}
