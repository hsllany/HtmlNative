package com.mozz.remoteview.parser.reader;

import com.mozz.remoteview.parser.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;


public class StreamReader implements CodeReader {

    public static final int CACHE_SIZE = 12;

    private char[] temp = new char[CACHE_SIZE];

    private int tempPos = -1;

    private int tempCount = 0;

    private int column = 1;

    private int line = 1;

    private char ch = ' ';

    private Reader mInputStream;

    public StreamReader() {

    }

    public StreamReader(Reader inputStream) {
        mInputStream = inputStream;
    }

    protected void setInputStream(Reader inputStream) {
        mInputStream = inputStream;
    }

    @Override
    public char nextCh() throws EOFException {
        try {
            if (tempPos == tempCount - 1) {
                fillInCache();
            }
            ch = temp[tempPos + 1];
            tempPos++;
            column++;

            if (ch == '\n' || ch == '\r') {
                this.line++;
                column = 1;
            }

            return ch;

        } catch (IOException e) {
            e.printStackTrace();
            EOFException eofException = new EOFException();
            eofException.initCause(e);
            close();
            throw eofException;

        }
    }

    private void fillInCache() throws IOException {
        if (mInputStream == null)
            throw new EOFException("input stream is null");

        int count = mInputStream.read(temp);

        if (count == -1) {
            throw new EOFException();
        }
        tempCount = count;
        tempPos = -1;
    }

    @Override
    public long line() {
        return line;
    }

    @Override
    public long column() {
        return column;
    }

    @Override
    public char current() {
        return ch;
    }

    @Override
    public void close() {
        Utils.closeQuitely(mInputStream);
    }
}
