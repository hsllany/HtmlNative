package com.mozz.remoteview.json.parser.reader;

import java.io.EOFException;
import java.io.IOException;

public class StringCodeReader implements CodeReader {
    private String code;
    private int position = 0;
    private int length = 0;
    private int line = 0;
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
            if (ch == '\n' || ch == 'r') {
                this.line++;
            }

            return ch;
        } else
            throw new EOFException("end of file");
    }

    @Override
    public int line() {
        return this.line;
    }

    @Override
    public char current() {
        return ch;
    }
}
