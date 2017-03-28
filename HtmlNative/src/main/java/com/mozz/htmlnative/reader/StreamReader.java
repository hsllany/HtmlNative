package com.mozz.htmlnative.reader;

import android.support.annotation.NonNull;
import android.util.Log;

import com.mozz.htmlnative.common.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;


public class StreamReader implements TextReader {

    private static final String TAG = StreamReader.class.getSimpleName();

    public static final int CACHE_SIZE = 1024 * 2;

    @NonNull
    private char[] temp = new char[CACHE_SIZE];

    private int tempPos = -1;

    private int tempCount = 0;

    private int column = 1;

    private int line = 1;

    private char ch = ' ';

    private Reader mInputStream;

    boolean meetEof = false;

    private long countOfRead = 0;

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
                if (meetEof) {
                    Log.w(TAG, "reach the end of the stream!");
                    throw new EOFException("Reach the end of stream!");
                }
                fillInCache();
            }
            ch = temp[tempPos + 1];
            tempPos++;
            column++;

            if (ch == '\n' || ch == '\r') {
                this.line++;
                column = 1;
            }

            countOfRead++;
            return ch;

        } catch (IOException e) {
            Log.w(TAG, "IOException EOF " + e.getMessage());
            e.printStackTrace();
            EOFException eofException = new EOFException();
            eofException.initCause(e);
            close();
            throw eofException;
        }
    }

    private void fillInCache() throws IOException {
        if (mInputStream == null) {
            Log.w(TAG, "input stream is null");
            throw new EOFException("input stream is null");
        }

        int count = mInputStream.read(temp);

        if (count == -1) {
            // If reach the end of stream, will add an ' ' to the end of the stream for lexer to
            // handle, then on the next round of nextCh(), an EOFException will be thrown.
            temp[0] = ' ';
            tempCount = 1;
            tempPos = -1;
            meetEof = true;
            return;
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
        Utils.closeQuietly(mInputStream);
    }

    @Override
    public long countOfRead() {
        return countOfRead;
    }
}
