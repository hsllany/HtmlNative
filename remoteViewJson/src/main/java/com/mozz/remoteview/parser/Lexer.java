package com.mozz.remoteview.parser;

import android.support.annotation.Nullable;

import com.mozz.remoteview.parser.reader.CodeReader;
import com.mozz.remoteview.parser.token.Token;
import com.mozz.remoteview.parser.token.Type;

import java.io.EOFException;


final class Lexer {

    private CodeReader mReader;

    private StringBuilder mBuffer = new StringBuilder();

    private int mLookFor = LK_NOTHING;

    private static final int LK_ELEMENT = 0x01;
    private static final int LK_NOTHING = 0;

    Lexer(CodeReader reader) {
        mReader = reader;
    }

    @Nullable
    Token scan() throws EOFException, SyntaxError {

        this.skipWhiteSpace();

        switch (peek()) {
            case '<':
                mLookFor = LK_NOTHING;
                next();
                return new Token(Type.LeftAngleBracket);
            case '"':
                next();
                return scanValue();
            case '>':
                mLookFor = LK_ELEMENT;
                next();
                return new Token(Type.RightAngleBracket);
            case '/':
                next();
                return new Token(Type.Slash);
            case '=':
                next();
                return new Token(Type.Equal);
        }

        if (isDigit(peek()) || peek() == '-') {
            return scanNumber();
        }

        if (isLetter(peek()) || peek() == '_') {
            if (mLookFor == LK_ELEMENT) {
                return scanElement();
            } else {
                return scanId();
            }

        }
        return null;
    }

    private Token scanNumber() throws EOFException, SyntaxError {
        int v = 0;
        boolean nagitive = false;
        if (peek() == '-') {
            nagitive = true;
            next();
        }

        if (!Lexer.isDigit(peek())) {
            throw new Error("Illegal word when reading Number!");
        }

        do {
            v = 10 * v + (peek() - '0');
            next();
        } while (isDigit(peek()));

        if (peek() != '.' && peek() != 'E' && peek() != 'e')
            return new Token(Type.Int, nagitive ? -v : v);

        double x = v, d = 10;
        if (peek() == '.') {
            for (; ; ) {
                next();
                if (!Lexer.isDigit(peek())) break;

                x = x + (peek() - '0') / d;
                d = d * 10;
            }
        }

        if (peek() == 'e' || peek() == 'E') {
            next();

            if (!Lexer.isDigit(peek()) && peek() != '-') {
                throw new SyntaxError("Illegal word when reading Number!", line());
            }
            boolean expIsNagitive = false;
            if (peek() == '-') {
                expIsNagitive = true;
                next();
            }

            int n = 0;
            do {
                n = 10 * n + (peek() - '0');
                next();
            } while (Lexer.isDigit(peek()));

            n = expIsNagitive ? -n : n;

            double exp = Math.pow(10, n);
            return new Token(Type.Double, nagitive ? (-x * exp) : (x * exp));

        } else {
            return new Token(Type.Double, nagitive ? -x : x);
        }
    }

    private Token scanId() throws EOFException {
        clearBuf();
        do {
            mBuffer.append(peek());
            next();
        } while (isLetter(peek()) || isDigit(peek()) || peek() == '.');

        return new Token(Type.Id, mBuffer.toString());
    }

    private Token scanValue() throws EOFException {
        clearBuf();

        do {
            mBuffer.append(peek());
            next();
        } while (peek() != '"');

        next();

        return new Token(Type.Value, mBuffer.toString());

    }

    private Token scanElement() throws EOFException {
        clearBuf();

        do {
            mBuffer.append(peek());
            next();
        } while (peek() != '<');
        mLookFor = LK_NOTHING;

        return new Token(Type.Element, mBuffer.toString());
    }


    private void skipWhiteSpace() throws EOFException {
        for (; ; ) {
            char ch = peek();
            if (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t' || ch == '\f' || ch == '\b') {
                next();
                continue;
            } else {
                break;
            }
        }
    }

    int line() {
        return mReader.line();
    }

    private char peek() {
        return mReader.current();
    }

    private void next() throws EOFException {
        this.mReader.nextCh();
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private void clearBuf() {
        mBuffer.delete(0, mBuffer.length());
    }
}
