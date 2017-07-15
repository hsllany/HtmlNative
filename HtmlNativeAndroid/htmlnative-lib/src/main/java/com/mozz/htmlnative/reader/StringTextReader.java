package com.mozz.htmlnative.reader;

import java.io.EOFException;

public class StringTextReader implements TextReader {

    private final char[] mChars;
    private int mPos = 0;
    private final int mLength;
    private long mLine = 1;
    private long mColumn = 1;
    private char mCurrent;

    public StringTextReader(String str) {
        mChars = str.toCharArray();
        mLength = mChars.length;

        mLine = 0;
        mColumn = 0;
    }

    @Override
    public char nextCh() throws EOFException {
        if (mPos + 1 == mLength) {
            mPos++;
            return ' ';
        } else if (mPos + 1 > mLength) {
            throw new EOFException();
        }

        mCurrent = mChars[mPos++];
        mColumn++;
        if (mCurrent == '\n' || mCurrent == '\r') {
            mLine++;
            mColumn = 1;
        }
        return mCurrent;
    }

    @Override
    public long line() {
        return mLine;
    }

    @Override
    public long column() {
        return mColumn;
    }

    @Override
    public char current() {
        return mCurrent;
    }

    @Override
    public void close() {
    }

    @Override
    public long countOfRead() {
        return mPos;
    }
}
