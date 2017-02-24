package com.mozz.remoteview.parser.reader;

import com.mozz.remoteview.parser.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class FileCodeReader implements CodeReader {

    private long line = 1;
    private long column = 1;
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
            column++;

            if (temp == -1) {
                throw new EOFException("reach the end of file");
            }
            ch = (char) temp;
            if (ch == '\n' || ch == '\r') {
                this.line++;
                column = 1;
            }

            return ch;
        } catch (IOException e) {
            close();
            e.printStackTrace();
            throw new EOFException(e.getMessage());
        }
    }


    @Override
    public long line() {
        return line;
    }

    @Override
    public char current() {
        return ch;
    }

    @Override
    public long column() {
        return column;
    }

    @Override
    public void close() {
        Utils.closeQuitely(mFileInputStream);
        Utils.closeQuitely(mFileReader);
    }
}
