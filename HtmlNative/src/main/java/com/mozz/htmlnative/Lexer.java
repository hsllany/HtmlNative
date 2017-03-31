package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mozz.htmlnative.token.Token;
import com.mozz.htmlnative.token.TokenType;
import com.mozz.htmlnative.reader.TextReader;

import java.io.EOFException;

import static com.mozz.htmlnative.HNLog.LEXER;


class Lexer {

    private static final String TAG = Lexer.class.getSimpleName();

    private TextReader mReader;

    @NonNull
    StringBuilder mBuffer = new StringBuilder();

    private int mLookFor = 0;

    private static final int LK_NOTHING = 1;
    private static final int LK_INNER = 1 << 1;

    private CharQueue mCacheQueue;
    private static final int CACHE_SIZE = 7;

    private int mReserved = 0;

    private char mCurrent = TextReader.INIT_CHAR;

    private boolean mIsInStyle = false;

    // Add for recognize code from Inner Element. If < script > is meet, than mLookForScript==3,
    // otherwise, mLookForScript < 3.
    private int mLookForScript = 0;

    private StringBuilder mLastCache = new StringBuilder();

    Lexer(TextReader reader) {
        mReader = reader;

        mCacheQueue = new CharQueue(CACHE_SIZE);

        lookFor(LK_NOTHING);
    }

    @Nullable
    Token scan() throws EOFException, HNSyntaxError {
        this.skipWhiteSpace();

        switch (peekWithLastCache()) {
            case '<':
                mLookForScript = 1;
                lookFor(LK_NOTHING);
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.StartAngleBracket, mReader.line(), mReader
                        .column());

            case '"':
                next();
                mLookForScript = 0;
                mLastCache.setLength(0);
                return scanValue();
            case '>':
                mLookForScript++;
                lookFor(LK_INNER);
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.EndAngleBracket, mReader.line(), mReader
                        .column());

            case '/':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.Slash, mReader.line(), mReader.column());

            case '=':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.Equal, mReader.line(), mReader.column());

            case '{':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.StartBrace, mReader.line(), mReader.column());

            case '}':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.EndBrace, mReader.line(), mReader.column());

            case '#':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.Hash, mReader.line(), mReader.column());

            case '.':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.Dot, mReader.line(), mReader.column());

            case ':':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.Colon, mReader.line(), mReader.column());

            case ';':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.Semicolon, mReader.line(), mReader.column());

            case '(':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.StartParen, mReader.line(), mReader.column());

            case ')':
                mLookForScript = 0;
                mLastCache.setLength(0);
                next();
                return Token.obtainToken(TokenType.EndParen, mReader.line(), mReader.column());


        }

        if (isLookingFor(LK_INNER) && mLookForScript < 3 && peek() != '<' && !mIsInStyle) {
            return scanInner();
        }

        if (isDigit(peek()) || peek() == '-') {
            mLookForScript = 0;
            return scanNumber();
        }

        if (isLetter(peek()) || peek() == '_') {
            return scanId();
        }

        HNLog.e(LEXER, "unknown token " + peek() + " at " + line() + "," + column());
        throw new HNSyntaxError("unknown token " + peek(), line(), column());
    }

    @Nullable
    Token scanNumber() throws EOFException, HNSyntaxError {
        mLastCache.setLength(0);
        long startColumn = mReader.column();
        long line = mReader.line();
        int v = 0;
        boolean negative = false;
        if (peek() == '-') {
            negative = true;
            next();
        }

        if (!Lexer.isDigit(peek())) {
            HNLog.e(LEXER, "Illegal word" + peek() + " when reading Number!");
            throw new HNSyntaxError("Illegal word when reading Number!", line, startColumn);
        }

        do {
            v = 10 * v + (peek() - '0');
            next();
        } while (isDigit(peek()));

        if (peek() != '.' && peek() != 'E' && peek() != 'e' && peek() != '%') {
            return Token.obtainToken(TokenType.Int, negative ? -v : v, line, startColumn);
        }

        if (peek() == '%') {
            next();
            return Token.obtainToken(TokenType.Double, negative ? -v / 100.f : v / 100.f, line,
                    startColumn, Token.EXTRA_NUMBER_PERSENTAGE);
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

        if (peek() == '%') {
            next();
            return Token.obtainToken(TokenType.Double, negative ? -x / 100.f : x / 100.f, line,
                    startColumn, Token.EXTRA_NUMBER_PERSENTAGE);
        }

        mLastCache.append(peek());
        if (peek() == 'e' || peek() == 'E') {
            next();

            if (!Lexer.isDigit(peek()) && peek() != '-') {
                return Token.obtainToken(TokenType.Double, negative ? -x : x, line, startColumn);
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
    Token scanId() throws EOFException {
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
            tokenContent = idStr;

        } else if (idStr.equalsIgnoreCase(TokenType.Script.toString())) {
            mLookForScript++;

            type = TokenType.Script;
            tokenContent = idStr;

        } else if (idStr.equalsIgnoreCase(TokenType.Head.toString())) {

            type = TokenType.Head;
            tokenContent = idStr;

        } else if (idStr.equalsIgnoreCase(TokenType.Meta.toString())) {

            type = TokenType.Meta;
            tokenContent = idStr;

        } else if (idStr.equalsIgnoreCase(TokenType.Link.toString())) {

            type = TokenType.Link;
            tokenContent = idStr;
        } else if (idStr.equalsIgnoreCase(TokenType.Html.toString())) {

            type = TokenType.Html;
            tokenContent = idStr;

        } else if (idStr.equalsIgnoreCase(TokenType.Title.toString())) {

            type = TokenType.Title;
            tokenContent = idStr;
        } else if (idStr.equalsIgnoreCase(TokenType.Style.toString())) {
            type = TokenType.Style;
            tokenContent = idStr;

            if (!mIsInStyle && peekHistory(6) == '<') {
                mIsInStyle = true;
            } else {
                mIsInStyle = false;
            }

        } else {
            tokenContent = idStr;
        }

        return Token.obtainToken(type, tokenContent, line, startColumn);

    }

    @Nullable
    Token scanValue() throws EOFException {
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
    Token scanInner() throws EOFException {
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
     * Called by {@link Parser#processScript(HNSegment)}, not by Lexer, the structure may ugly
     * but simple to implement.  Because Lexer
     * can't tell whether it's an script or not, only parser has such ability.
     * <br/>
     * This function read the script inside "script" tag, no matter it's JavaScript or Lua or
     * Other language. It detect the end of script by reading '<' and '/' continuously outside the
     * quotation;
     *
     * @return ScriptInfo string
     * @throws EOFException
     * @throws HNSyntaxError
     */
    Token scanScript() throws EOFException, HNSyntaxError {
        long startColumn = mReader.column();
        long line = mReader.line();

        if (currentPositionInFile() < CACHE_SIZE) {
            throw new HNSyntaxError("wrong status, too early for script.", line, startColumn);
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


    protected void skipWhiteSpace() throws EOFException {
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

    protected char peek() {
        return mCurrent;
    }

    protected char peekWithLastCache() {
        if (mLastCache.length() > 0) {
            return mLastCache.charAt(0);
        }
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

    void next() throws EOFException {
        if (mReserved > 0) {
            mCurrent = mCacheQueue.peek(CACHE_SIZE - mReserved - 1);
            mReserved--;
            return;
        }
        this.mReader.nextCh();
        mCurrent = this.mReader.current();
        mCacheQueue.push(peek());
        HNLog.d(LEXER, "next-> " + peek());
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

    protected void clearBuf() {
        mBuffer.setLength(0);
        if (mLastCache.length() > 0) {
            mBuffer.append(mLastCache);
            mLastCache.setLength(0);
        }
    }
}
