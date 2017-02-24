package com.mozz.remoteview.parser.reader;

import java.io.EOFException;

public class StringCodeReader implements CodeReader {
    private String code;
    private int position = 0;
    private int length = 0;
    private long line = 1;
    private long column = 1;
    private char ch = ' ';


    public StringCodeReader(String str) {
        code = str + ' ';
        length = code.length();
        position = 0;
    }

    @Override
    public char nextCh() throws EOFException {
        if (position < length) {
            ch = code.charAt(position++);
            this.column++;
            if (ch == '\n' || ch == '\r') {
                this.line++;
                this.column = 1;
            }

            return ch;
        } else
            throw new EOFException("end of file");
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

    }
}
