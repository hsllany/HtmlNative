package com.mozz.remoteview.parser.reader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class FileCodeReader implements CodeReader {

    private int line;

    private InputStream mFileInputStream;

    private Reader mFileReader;

    private boolean isFirst = true;

    private char ch;

    private int temp;

    public FileCodeReader(InputStream file) {
        mFileInputStream = file;
    }

    @Override
    public char nextCh() throws EOFException {

        if (isFirst) {
            if (mFileReader == null) {
                mFileReader = new InputStreamReader(mFileInputStream);
            }
            isFirst = false;
        }


        try {
            temp = mFileReader.read();

            if (temp == -1) {
                throw new EOFException("reach the end of file");
            }
            ch = (char) temp;
            if (ch == '\n' || ch == '\r') {
                this.line++;
            }

            return ch;
        } catch (IOException e) {
            e.printStackTrace();
            throw new EOFException(e.getMessage());
        }
    }


    @Override
    public int line() {
        return line;
    }

    @Override
    public char current() {
        return ch;
    }
}
