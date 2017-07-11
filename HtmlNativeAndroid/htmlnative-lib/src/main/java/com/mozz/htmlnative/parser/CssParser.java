package com.mozz.htmlnative.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSegment;
import com.mozz.htmlnative.css.StyleSheet;
import com.mozz.htmlnative.css.selector.AnySelector;
import com.mozz.htmlnative.css.selector.ClassSelector;
import com.mozz.htmlnative.css.selector.CssSelector;
import com.mozz.htmlnative.css.selector.IdSelector;
import com.mozz.htmlnative.css.selector.TypeSelector;
import com.mozz.htmlnative.exception.HNSyntaxError;
import com.mozz.htmlnative.parser.syntaxexc.SyntaxErrorHandler;
import com.mozz.htmlnative.parser.syntaxexc.SyntaxExceptionSource;
import com.mozz.htmlnative.parser.token.Token;
import com.mozz.htmlnative.parser.token.TokenType;

import java.io.EOFException;
import java.util.Map;

import static com.mozz.htmlnative.parser.StyleItemParser.parseKey;
import static com.mozz.htmlnative.parser.StyleItemParser.parseStyleSingle;

/**
 * @author Yang Tao, 17/3/26.
 */

public final class CssParser implements SyntaxExceptionSource {

    private static final int SELECTOR_HASH = 1 << 7;
    private static final int SELECTOR_DOT = 1 << 8;
    private static final int SELECTOR_ID = 1;
    private static final int SELECTOR_STAR = 1 << 16;
    private static final int SELECTOR_CLASS = 1 << 1;
    private static final int SELECTOR_TYPE = 1 << 2;
    private static final int START_BRACE = 1 << 3;
    private static final int END_BRACE = 1 << 4;
    private static final int KEY = 1 << 5;
    private static final int VALUE_STRING = 1 << 6;
    private static final int VALUE_INT = 1 << 9;
    private static final int VALUE_DOUBLE = 1 << 10;
    private static final int VALUE_HASH = 1 << 11;
    private static final int VALUE_START_PAREN = 1 << 12;
    private static final int VALUE_END_PAREN = 1 << 13;
    private static final int COLON = 1 << 7;
    private static final int SEMICOLON = 1 << 8;
    private static final int COMMA = 1 << 14;
    private static final int END_ANGLE_BRACKET = 1 << 15;

    private static final int SELECTOR_START = SELECTOR_HASH | SELECTOR_TYPE | SELECTOR_DOT |
            SELECTOR_STAR;
    private static final int VALUE = VALUE_STRING | VALUE_INT | VALUE_DOUBLE | VALUE_HASH |
            VALUE_START_PAREN | VALUE_END_PAREN;

    private int lookFor;

    private static final int CHAIN_DESCENDANT = 0x01;
    private static final int CHAIN_CHILD = 0x02;
    private static final int CHAIN_GROUP = 0x03;


    @Nullable
    private Token mCurToken;

    private final CssLexer lexer;

    private Map<String, Object> styleCache;

    private SyntaxErrorHandler mSyntaxErrorHandler;

    CssParser(Lexer lexer, Parser parentParser, SyntaxErrorHandler errorHandler) {
        this.lexer = new CssLexer(lexer);
        this.styleCache = parentParser.getStyleCache();
        mSyntaxErrorHandler = errorHandler;
        mSyntaxErrorHandler.setSource(this);
    }

    /**
     * Static Method to parse inline style into {@code Map<String, Object>}
     *
     * @param styleString, inline style string to parse
     * @param bufferToUse, buffer to use, may overwrite its content
     * @param out,         out parameter, to store the result of parsed data.
     */
    public static void parseInlineStyle(@NonNull String styleString, StringBuilder bufferToUse,
                                        Map<String, Object> out) {
        bufferToUse.setLength(0);

        String key = null;

        out.clear();

        boolean inBracket = false;
        for (int i = 0; i < styleString.length(); i++) {
            char c = styleString.charAt(i);

            if (c == '(') {
                inBracket = true;
                bufferToUse.append(c);
            } else if (c == ')') {
                inBracket = false;
                bufferToUse.append(c);
            } else if (c == ';') {
                Object value = out.get(parseKey(key));
                StyleHolder parsedStyle;
                if (value != null) {
                    parsedStyle = parseStyleSingle(key, bufferToUse.toString(), value);
                } else {
                    parsedStyle = parseStyleSingle(key, bufferToUse.toString(), null);
                }
                out.put(parsedStyle.key, parsedStyle.obj);
                bufferToUse.setLength(0);
            } else if (c == ':' && !inBracket) {
                key = bufferToUse.toString();
                bufferToUse.setLength(0);
            } else {
                if (c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\f' || c == '\b') {
                    continue;
                }
                bufferToUse.append(c);
            }
        }

        if (key != null) {
            Object value = out.get(parseKey(key));
            StyleHolder parsedStyle;
            if (value != null) {
                parsedStyle = parseStyleSingle(key, bufferToUse.toString(), value);
            } else {
                parsedStyle = parseStyleSingle(key, bufferToUse.toString(), null);
            }
            out.put(parsedStyle.key, parsedStyle.obj);
        }

        bufferToUse.setLength(0);
    }

    void process(HNSegment segment) throws EOFException, HNSyntaxError {
        StyleSheet styleSheet = segment.getStyleSheet();

        lookFor(SELECTOR_START);

        CssSelector cssSelector = null;

        String keyCache = null;

        int chainType = CHAIN_DESCENDANT;

        while (true) {
            scan();

            switch (mCurToken.type()) {
                case Comma:
                    check(COMMA);
                    lookFor(SELECTOR_START);
                    chainType = CHAIN_GROUP;
                    break;

                case EndAngleBracket:
                    check(END_ANGLE_BRACKET);
                    lookFor(SELECTOR_START);
                    chainType = CHAIN_CHILD;
                    break;

                case Hash:
                    check(SELECTOR_HASH | VALUE);
                    lookFor(SELECTOR_ID);
                    break;

                case Id:
                    String idValue = mCurToken.stringValue();
                    // tag selector should be in the first position of whole if-statement
                    if (isLookingFor(SELECTOR_TYPE)) {
                        if (cssSelector == null) {
                            cssSelector = new TypeSelector(idValue);
                            styleSheet.register(cssSelector);
                        } else {
                            CssSelector groupOne = new TypeSelector(idValue);
                            if (chain(cssSelector, groupOne, chainType)) {
                                styleSheet.putSelector(cssSelector);
                                cssSelector = groupOne;
                            }
                        }
                        lookFor(START_BRACE | SELECTOR_START | COMMA | END_ANGLE_BRACKET);
                        chainType = CHAIN_DESCENDANT;
                    } else if (isLookingFor(SELECTOR_CLASS)) {
                        if (cssSelector == null) {
                            cssSelector = new ClassSelector(idValue);
                            styleSheet.register(cssSelector);
                        } else {

                            CssSelector groupOne = new ClassSelector(idValue);
                            if (chain(cssSelector, groupOne, chainType)) {
                                styleSheet.putSelector(cssSelector);
                                cssSelector = groupOne;
                            }

                        }
                        lookFor(START_BRACE | SELECTOR_START | COMMA | END_ANGLE_BRACKET);
                        chainType = CHAIN_DESCENDANT;
                    } else if (isLookingFor(SELECTOR_ID)) {
                        if (cssSelector == null) {
                            cssSelector = new IdSelector(idValue);
                            styleSheet.register(cssSelector);
                        } else {

                            CssSelector groupOne = new IdSelector(idValue);
                            if (chain(cssSelector, groupOne, chainType)) {
                                styleSheet.putSelector(cssSelector);
                                cssSelector = groupOne;
                            }
                        }
                        lookFor(START_BRACE | SELECTOR_START | COMMA | END_ANGLE_BRACKET);
                        chainType = CHAIN_DESCENDANT;
                    } else if (isLookingFor(KEY)) {
                        check(KEY);
                        keyCache = idValue;
                        lookFor(COLON);
                    }

                    break;
                case Dot:
                    check(SELECTOR_DOT);
                    lookFor(SELECTOR_CLASS);
                    break;

                case Star:
                    check(SELECTOR_STAR);
                    lookFor(START_BRACE | SELECTOR_START | COMMA | END_ANGLE_BRACKET);
                    if (cssSelector == null) {
                        cssSelector = new AnySelector();
                        styleSheet.register(cssSelector);
                    } else {
                        CssSelector groupOne = new AnySelector();
                        if (chain(cssSelector, groupOne, chainType)) {
                            styleSheet.putSelector(cssSelector);
                            cssSelector = groupOne;
                        }
                    }

                    chainType = CHAIN_DESCENDANT;
                    break;
                case Colon:
                    check(COLON);
                    lookFor(VALUE);
                    shouldScanValue = true;
                    break;

                case StartBrace:
                    check(START_BRACE);
                    lookFor(KEY | END_BRACE);
                    break;

                case EndBrace:
                    check(END_BRACE);
                    lookFor(SELECTOR_START);
                    styleSheet.putSelector(cssSelector);
                    // put all the attr in styleSheet
                    for (Map.Entry<String, Object> entry : styleCache.entrySet()) {
                        styleSheet.put(cssSelector, entry.getKey(), entry.getValue());
                    }

                    styleCache.clear();

                    cssSelector = null;
                    break;

                case Value:
                    check(VALUE);

                    Object value = styleCache.get(parseKey(keyCache));
                    StyleHolder parsedStyle;
                    if (value != null) {
                        parsedStyle = parseStyleSingle(keyCache, mCurToken.stringValue(), value);
                    } else {
                        parsedStyle = parseStyleSingle(keyCache, mCurToken.stringValue(), null);
                    }
                    styleCache.put(parsedStyle.key, parsedStyle.obj);
                    lookFor(VALUE | END_BRACE | SEMICOLON);

                    break;
                case Semicolon:
                    check(SEMICOLON);
                    lookFor(END_BRACE | KEY);
                    break;

                // Below is special case, to handle the class or id selector which have the same
                // name with Head, Meta, Script, Template, Body, Link, Style, Html and Title. The
                // process is the same with Id token.
                case Head:
                case Meta:
                case Script:
                case Template:
                case Body:
                case Link:
                case Style:
                case Html:
                case Title:
                    check(SELECTOR_CLASS | SELECTOR_ID | SELECTOR_TYPE);
                    if (isLookingFor(SELECTOR_CLASS)) {
                        if (cssSelector == null) {
                            cssSelector = new ClassSelector(mCurToken.stringValue());
                            styleSheet.register(cssSelector);
                        } else {
                            CssSelector groupOne = new ClassSelector(mCurToken.stringValue());
                            if (chain(cssSelector, groupOne, chainType)) {
                                styleSheet.putSelector(cssSelector);
                                cssSelector = groupOne;
                            }
                        }
                    } else if (isLookingFor(SELECTOR_ID)) {
                        if (cssSelector == null) {
                            cssSelector = new IdSelector(mCurToken.stringValue());
                            styleSheet.register(cssSelector);
                        } else {
                            CssSelector groupOne = new IdSelector(mCurToken.stringValue());
                            if (chain(cssSelector, groupOne, chainType)) {
                                styleSheet.putSelector(cssSelector);
                                cssSelector = groupOne;
                            }
                        }
                    } else if (isLookingFor(SELECTOR_TYPE)) {
                        if (cssSelector == null) {
                            cssSelector = new TypeSelector(mCurToken.stringValue());
                            styleSheet.register(cssSelector);
                        } else {
                            CssSelector groupOne = new TypeSelector(mCurToken.stringValue());
                            if (chain(cssSelector, groupOne, chainType)) {
                                styleSheet.putSelector(cssSelector);
                                cssSelector = groupOne;
                            }
                        }
                    }
                    lookFor(START_BRACE | SELECTOR_START);
                    break;

                case StartAngleBracket:
                    check(SELECTOR_START);
                    scan();
                    if (mCurToken.type() == TokenType.Slash) {
                        HNLog.d(HNLog.CSS_PARSER, styleSheet.toString());

                        return;
                    }
                    // if parse process didn't end, then there is a syntax error.
                default:
                    mSyntaxErrorHandler.throwException("unknown token " + mCurToken.toString() +
                            " when " + "parsing css", mCurToken.getLine(), mCurToken.getColumn());
            }
        }
    }

    private boolean isLookingFor(int status) {
        return (lookFor & status) != 0;
    }

    private void lookFor(int status) {
        lookFor = 0;
        lookFor |= status;
    }

    private void scan() throws EOFException, HNSyntaxError {
        if (mCurToken != null) {
            mCurToken.recycle();
        }
        mCurToken = lexer.scan();

        HNLog.d(HNLog.CSS_PARSER, "StyleSheet -> next is " + mCurToken.toString());
    }

    private boolean shouldScanValue = false;

    @Override
    public long getLine() {
        return lexer.getLine();
    }

    @Override
    public long getColumn() {
        return lexer.getColumn();
    }

    @Override
    public void onSyntaxException() throws HNSyntaxError, EOFException {
        scan();
    }

    private class CssLexer implements SyntaxExceptionSource {

        private Lexer lexer;

        private StringBuilder buffer = new StringBuilder();

        CssLexer(Lexer lexer) {
            super();
            this.lexer = lexer;
        }

        @Nullable
        Token scan() throws EOFException, HNSyntaxError {
            lexer.skipWhiteSpace();

            if (shouldScanValue) {
                return scanValue();
            } else if (peek() == '-') {
                // hook the - case, to handle the style name such as -webkit-**.
                return scanIdWithMinus();
            } else {
                return lexer.scan();
            }
        }

        char peek() {
            return lexer.peek();
        }

        Token scanValue() throws EOFException {
            long startColumn = lexer.getColumn();
            long line = lexer.getLine();

            lexer.skipWhiteSpace();

            buffer.setLength(0);

            if (peek() == ';') {
                lexer.next();
                return Token.obtainToken(TokenType.Value, "", line, startColumn);
            }

            do {
                buffer.append(peek());
                lexer.next();

                if (peek() == ';' || peek() == '}') {
                    break;
                }
            } while (true);

            shouldScanValue = false;

            return Token.obtainToken(TokenType.Value, buffer.toString(), line, startColumn);
        }

        Token scanIdWithMinus() throws EOFException {
            long startColumn = lexer.getColumn();
            long line = lexer.getLine();

            buffer.setLength(0);

            do {
                buffer.append(peek());
                lexer.next();
            }
            while (Lexer.isLetter(peek()) || Lexer.isDigit(peek()) || peek() == '.' || peek() ==
                    '-' || peek() == '_');

            String idStr = buffer.toString();

            TokenType type = TokenType.Id;

            return Token.obtainToken(type, buffer.toString(), line, startColumn);
        }

        @Override
        public long getLine() {
            return lexer.getLine();
        }

        @Override
        public long getColumn() {
            return lexer.getColumn();
        }

        @Override
        public void onSyntaxException() throws EOFException, HNSyntaxError {
            this.lexer.onSyntaxException();
        }
    }


    private void check(int status) throws HNSyntaxError, EOFException {
        if (!isLookingFor(status)) {
            HNLog.d(HNLog.CSS_PARSER, " Looking for " + lookForToString(status) + ", but " +
                    "currently is " + lookForToString(this.lookFor));
            mSyntaxErrorHandler.throwException(" Looking for " + lookForToString(status) + ", " +
                    "but" + " " + "currently is " + lookForToString(this.lookFor));
        }
    }

    private static boolean chain(CssSelector root, CssSelector newCss, int chainType) {
        switch (chainType) {
            case CHAIN_CHILD:
                root.chainChild(newCss, false);
                return false;
            case CHAIN_GROUP:
                // if chain group happens, should return true, than root will become the newCss
                root.chainGroup(newCss);
                return true;
            case CHAIN_DESCENDANT:
                root.chainChild(newCss, true);
                return false;
            default:
                throw new IllegalStateException();
        }
    }

    private static String lookForToString(int lookFor) {
        StringBuilder sb = new StringBuilder("[ ");

        if ((lookFor & SELECTOR_HASH) != 0) {
            sb.append("# ");
        }

        if ((lookFor & SELECTOR_DOT) != 0) {
            sb.append(". ");
        }

        if ((lookFor & SELECTOR_ID) != 0) {
            sb.append("id ");
        }

        if ((lookFor & SELECTOR_CLASS) != 0) {
            sb.append("class ");
        }

        if ((lookFor & SELECTOR_TYPE) != 0) {
            sb.append("type ");
        }

        if ((lookFor & START_BRACE) != 0) {
            sb.append("{ ");
        }

        if ((lookFor & END_BRACE) != 0) {
            sb.append("} ");
        }

        if ((lookFor & KEY) != 0) {
            sb.append("cssPropertyName ");
        }

        if ((lookFor & VALUE) != 0) {
            sb.append("cssPropertyValue ");
        }

        if ((lookFor & COLON) != 0) {
            sb.append(": ");
        }

        if ((lookFor & SEMICOLON) != 0) {
            sb.append("; ");
        }

        sb.append(" ]");

        return sb.toString();

    }

    static class StyleHolder {
        public String key;
        Object obj;
        public String cacheKey;
    }
}
