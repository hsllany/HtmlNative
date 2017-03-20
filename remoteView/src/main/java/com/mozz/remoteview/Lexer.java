package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mozz.remoteview.reader.TextReader;
import com.mozz.remoteview.token.Token;
import com.mozz.remoteview.token.TokenType;

import java.io.EOFException;


final class Lexer {

    private TextReader mReader;

    @NonNull
    private StringBuilder mBuffer = new StringBuilder();

    private int mLookFor = 0;

    private static final int LK_NOTHING = 1;
    private static final int LK_INNER = 1 << 1;

    private CharQueue mCacheQueue;
    private static final int CACHE_SIZE = 5;

    private int mReserved = 0;

    private char mCurrent = TextReader.INIT_CHAR;

    // Add for recognize code from Inner Element. If < script > is meet, than mLookForScript==3,
    // otherwise, mLookForScript < 3.
    private int mLookForScript = 0;

    Lexer(TextReader reader) {
        mReader = reader;

        mCacheQueue = new CharQueue(CACHE_SIZE);

        lookFor(LK_NOTHING);
    }

    @Nullable
    Token scan() throws EOFException, RVSyntaxError {
        this.skipWhiteSpace();

        switch (peek()) {
            case '<':
                mLookForScript = 1;
                lookFor(LK_NOTHING);
                next();
                return Token.obtainToken(TokenType.StartAngleBracket, mReader.line(), mReader
                        .column());

            case '"':
                next();
                mLookForScript = 0;
                return scanValue();
            case '>':
                mLookForScript++;
                lookFor(LK_INNER);
                next();
                return Token.obtainToken(TokenType.EndAngleBracket, mReader.line(), mReader
                        .column());

            case '/':
                mLookForScript = 0;
                next();
                return Token.obtainToken(TokenType.Slash, mReader.line(), mReader.column());

            case '=':
                mLookForScript = 0;
                next();
                return Token.obtainToken(TokenType.Equal, mReader.line(), mReader.column());
        }

        if (isLookingFor(LK_INNER) && mLookForScript < 3 && peek() != '<') {
            return scanInner();
        }

        if (isDigit(peek()) || peek() == '-') {
            mLookForScript = 0;
            return scanNumber();
        }

        if (isLetter(peek()) || peek() == '_') {
            return scanId();
        }

        throw new RVSyntaxError("unknown token " + peek(), line(), column());
    }

    @Nullable
    private Token scanNumber() throws EOFException, RVSyntaxError {
        long startColumn = mReader.column();
        long line = mReader.line();
        int v = 0;
        boolean negative = false;
        if (peek() == '-') {
            negative = true;
            next();
        }

        if (!Lexer.isDigit(peek())) {
            throw new RVSyntaxError("Illegal word when reading Number!", line, startColumn);
        }

        do {
            v = 10 * v + (peek() - '0');
            next();
        } while (isDigit(peek()));

        if (peek() != '.' && peek() != 'E' && peek() != 'e') {
            return Token.obtainToken(TokenType.Int, negative ? -v : v, line, startColumn);
        }

        double x = v, d = 10;
        if (peek() == '.') {
            for (; ; ) {
                next();
                if (!Lexer.isDigit(peek())) {
                    break;
                }

                x = x + (peek() - '0') / d;
                d = d * 10;
            }
        }

        if (peek() == 'e' || peek() == 'E') {
            next();

            if (!Lexer.isDigit(peek()) && peek() != '-') {
                throw new RVSyntaxError("Illegal word when reading Number!", line, startColumn);
            }
            boolean expIsNegative = false;
            if (peek() == '-') {
                expIsNegative = true;
                next();
            }

            int n = 0;
            do {
                n = 10 * n + (peek() - '0');
                next();
            } while (Lexer.isDigit(peek()));

            n = expIsNegative ? -n : n;

            double exp = Math.pow(10, n);
            return Token.obtainToken(TokenType.Double, negative ? (-x * exp) : (x * exp), line,
                    startColumn);

        } else {
            return Token.obtainToken(TokenType.Double, negative ? -x : x, line, startColumn);
        }
    }

    @Nullable
    private Token scanId() throws EOFException {
        long startColumn = mReader.column();
        long line = mReader.line();

        clearBuf();
        do {
            mBuffer.append(peek());
            next();
        }
        while (isLetter(peek()) || isDigit(peek()) || peek() == '.' || peek() == '-' || peek() ==
                '_');

        String idStr = mBuffer.toString();

        TokenType type = TokenType.Id;
        String tokenContent;

        if (idStr.equalsIgnoreCase(TokenType.Template.toString()) || idStr.equalsIgnoreCase
                (TokenType.Body.toString())) {

            type = TokenType.Template;
            tokenContent = type.toString();

        } else if (idStr.equalsIgnoreCase(TokenType.Script.toString())) {
            mLookForScript++;

            type = TokenType.Script;
            tokenContent = type.toString();

        } else if (idStr.equalsIgnoreCase(TokenType.Head.toString())) {

            type = TokenType.Head;
            tokenContent = type.toString();

        } else if (idStr.equalsIgnoreCase(TokenType.Meta.toString())) {

            type = TokenType.Meta;
            tokenContent = type.toString();

        } else if (idStr.equalsIgnoreCase(TokenType.Link.toString())) {

            type = TokenType.Link;
            tokenContent = type.toString();
        } else if (idStr.equalsIgnoreCase(TokenType.Html.toString())) {

            type = TokenType.Html;
            tokenContent = type.toString();

        } else if (idStr.equalsIgnoreCase(TokenType.Title.toString())) {

            type = TokenType.Title;
            tokenContent = type.toString();
        } else {
            tokenContent = idStr;
        }

        return Token.obtainToken(type, tokenContent, line, startColumn);

    }

    @Nullable
    private Token scanValue() throws EOFException {
        long startColumn = mReader.column();
        long line = mReader.line();

        clearBuf();

        if (peek() == '"') {
            next();
            return Token.obtainToken(TokenType.Value, "", line, startColumn);
        }

        do {
            mBuffer.append(peek());
            next();

            // handling the '\"' case
            if (peek() == '\\') {
                next();
                if (peek() != '"') {
                    mBuffer.append('\\');
                }
            } else if (peek() == '"') {
                break;
            }
        } while (true);

        next();

        return Token.obtainToken(TokenType.Value, mBuffer.toString(), line, startColumn);

    }

    @Nullable
    private Token scanInner() throws EOFException {
        long startColumn = mReader.column();
        long line = mReader.line();

        clearBuf();

        do {
            mBuffer.append(peek());
            next();

            if (peek() == '\\') {
                next();
                if (peek() != '<') {
                    mBuffer.append('\\');
                }
            } else if (peek() == '<') {
                break;
            }

            //TODO 考虑其他的情况，这里只会添加一个空格
            if (skipWhiteSpaceInner()) {
                mBuffer.append(' ');
            }

        } while (peek() != '<');

        lookFor(LK_NOTHING);

        char lastChar = mBuffer.charAt(mBuffer.length() - 1);
        if (lastChar == '\n' || lastChar == '\r') {
            mBuffer.deleteCharAt(mBuffer.length() - 1);
        }
        return Token.obtainToken(TokenType.Inner, mBuffer.toString(), line, startColumn);
    }

    /**
     * Called by {@link Parser#processScript(RVSegment)}, not by Lexer, the structure may ugly
     * but simple to implement.  Because Lexer
     * can't tell whether it's an script or not, only parser has such ability.
     * <br/>
     * This function read the script inside "script" tag, no matter it's JavaScript or Lua or
     * Other language. It detect the end of script by reading '<' and '/' continuously outside the
     * quotation;
     *
     * @return ScriptInfo string
     * @throws EOFException
     * @throws RVSyntaxError
     */
    Token scanScript() throws EOFException, RVSyntaxError {
        long startColumn = mReader.column();
        long line = mReader.line();

        if (currentPositionInFile() < CACHE_SIZE) {
            throw new RVSyntaxError("wrong status, too early for script.", line, startColumn);
        }


        clearBuf();

        next();
        next();

        // 0 no in any quota, 1 for quotation, 2 for single quotation
        byte inQuotation = 0;
        while (true) {
            if (inQuotation == 0 && peekHistory(0) == '/' && peekHistory(1) == '<') {
                mReserved = 2;
                break;
            }
            char ch = peekHistory(2);

            if (inQuotation == 0) {
                if (ch == '"') {
                    inQuotation = 1;
                } else if (ch == '\'') {
                    inQuotation = 2;
                }
            } else {
                if (inQuotation == 1) {
                    if (ch == '"' && peekHistory(4) != '\\') {
                        inQuotation = 0;
                    }
                } else if (inQuotation == 2) {
                    if (ch == '\'' && peekHistory(4) != '\\') {
                        inQuotation = 0;
                    }
                }
            }

            mBuffer.append(ch);
            next();
        }

        return Token.obtainToken(TokenType.Script, mBuffer.toString(), line, startColumn);
    }


    private boolean skipWhiteSpaceInner() throws EOFException {
        boolean meet = false;
        for (; ; ) {
            char ch = peek();
            if (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t' || ch == '\f' || ch == '\b') {
                if (!meet) {
                    meet = true;
                }
                next();
            } else {
                break;
            }
        }

        return meet;
    }


    private void skipWhiteSpace() throws EOFException {
        for (; ; ) {
            char ch = peek();
            if (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t' || ch == '\f' || ch == '\b') {
                next();
            } else {
                break;
            }
        }
    }

    void close() {
        if (mReader != null) {
            mReader.close();
        }
    }

    long line() {
        return mReader.line();
    }

    long column() {
        return mReader.column();
    }

    private char peek() {
        return mCurrent;
    }

    private long currentPositionInFile() {
        return mReader.countOfRead();
    }


    /**
     * @param historyBackCount must be smaller than {@link Lexer#CACHE_SIZE}
     * @return history char saved in {@link Lexer#mCacheQueue}
     */
    private char peekHistory(int historyBackCount) {
        if (historyBackCount > CACHE_SIZE) {
            throw new IllegalArgumentException("HistoryBackCount must be smaller than CACHE_SIZE " +
                    "(" + CACHE_SIZE + ")");
        }

        return mCacheQueue.peek(CACHE_SIZE - historyBackCount - 1);
    }

    private void next() throws EOFException {
        if (mReserved > 0) {
            mCurrent = mCacheQueue.peek(CACHE_SIZE - mReserved - 1);
            mReserved--;
            return;
        }
        this.mReader.nextCh();
        mCurrent = this.mReader.current();
        mCacheQueue.push(peek());
        EventLog.writeEvent(EventLog.TAG_LEXER, "next to " + peek());
    }

    private void lookFor(int status) {
        mLookFor = 0;
        mLookFor |= status;
    }

    private boolean isLookingFor(int status) {
        return (mLookFor & status) != 0;
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private void clearBuf() {
        mBuffer.setLength(0);
    }
}
