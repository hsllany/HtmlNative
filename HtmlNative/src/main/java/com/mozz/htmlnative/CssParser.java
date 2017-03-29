package com.mozz.htmlnative;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mozz.htmlnative.css.ClassSelector;
import com.mozz.htmlnative.css.CssSelector;
import com.mozz.htmlnative.css.IdSelector;
import com.mozz.htmlnative.css.TypeSelector;
import com.mozz.htmlnative.token.Token;
import com.mozz.htmlnative.token.TokenType;

import java.io.EOFException;

import static com.mozz.htmlnative.Parser.parseStyleSingle;

/**
 * @author Yang Tao, 17/3/26.
 */

final class CssParser {

    private static final String TAG = CssParser.class.getSimpleName();

    private static final int SELECTOR_HASH = 1 << 7;
    private static final int SELECTOR_DOT = 1 << 8;
    private static final int SELECTOR_ID = 1;
    private static final int SELECTOR_CLASS = 1 << 1;
    private static final int SELECTOR_TAG = 1 << 2;
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

    private static final int SELECTOR_START = SELECTOR_HASH | SELECTOR_TAG | SELECTOR_DOT;
    private static final int VALUE = VALUE_STRING | VALUE_INT | VALUE_DOUBLE | VALUE_HASH |
            VALUE_START_PAREN | VALUE_END_PAREN;

    private int lookFor;

    @Nullable
    private Token mCurToken;

    private final Lexer lexer;

    StringBuilder mValueCache = new StringBuilder();

    CssParser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void process(HNSegment segment) throws EOFException, HNSyntaxError {
        lookFor(SELECTOR_START);

        CssSelector cssSelector = null;

        String keyCache = null;

        mValueCache.setLength(0);

        boolean meetSemicolon = false;

        while (true) {
            scan();

            switch (mCurToken.type()) {
                case Hash:
                    check(SELECTOR_HASH | VALUE);
                    if ((lookFor & VALUE) != 0) {
                        appendToValueCacheWisely('#');
                    } else {
                        lookFor(SELECTOR_ID);
                    }
                    break;
                case Id:
                    // tag selector should be in the first position of whole if-statement
                    if (isLookingFor(SELECTOR_TAG)) {
                        if (cssSelector == null) {
                            cssSelector = new TypeSelector(mCurToken.stringValue());
                            segment.mCss.newAttr(cssSelector);
                        } else {
                            cssSelector.chain(new TypeSelector(mCurToken.stringValue()));
                        }
                        lookFor(START_BRACE | SELECTOR_START);
                    } else if (isLookingFor(SELECTOR_CLASS)) {
                        if (cssSelector == null) {
                            cssSelector = new ClassSelector(mCurToken.stringValue());
                            segment.mCss.newAttr(cssSelector);
                        } else {
                            cssSelector.chain(new ClassSelector(mCurToken.stringValue()));
                        }
                        lookFor(START_BRACE | SELECTOR_START);
                    } else if (isLookingFor(SELECTOR_ID)) {
                        if (cssSelector == null) {
                            cssSelector = new IdSelector(mCurToken.stringValue());
                            segment.mCss.newAttr(cssSelector);
                        } else {
                            cssSelector.chain(new IdSelector(mCurToken.stringValue()));
                        }
                        lookFor(START_BRACE | SELECTOR_START);
                    } else if (isLookingFor(KEY)) {
                        check(KEY);
                        keyCache = mCurToken.stringValue();
                        lookFor(COLON);
                        meetSemicolon = false;
                    } else if (isLookingFor(VALUE)) {
                        check(VALUE);
                        appendToValueCacheWisely(mCurToken.stringValue());
                        lookFor(SEMICOLON | END_BRACE | VALUE);
                    }

                    break;
                case Dot:
                    check(SELECTOR_DOT);
                    lookFor(SELECTOR_CLASS);
                    break;

                case Colon:
                    check(COLON);
                    lookFor(VALUE);
                    break;

                case StartBrace:
                    check(START_BRACE);
                    lookFor(KEY);
                    break;

                case EndBrace:
                    check(END_BRACE);
                    lookFor(SELECTOR_START);
                    if (!meetSemicolon) {
                        segment.mCss.putAttr(cssSelector, keyCache, parseStyleSingle(keyCache,
                                mValueCache.toString()));

                        mValueCache.setLength(0);
                        meetSemicolon = false;
                    }
                    segment.mCss.putSelector(cssSelector);
                    cssSelector = null;
                    break;

                case StartParen:
                case EndParen:
                    check(VALUE);
                    appendToValueCacheWisely(mCurToken.type().toString());
                    lookFor(VALUE | END_BRACE | SEMICOLON);
                    break;
                case Value:
                    check(VALUE);
                    appendToValueCacheWisely(mCurToken.stringValue());
                    lookFor(VALUE | END_BRACE | SEMICOLON);
                    break;
                case Semicolon:
                    check(SEMICOLON);
                    segment.mCss.putAttr(cssSelector, keyCache, parseStyleSingle(keyCache,
                            mValueCache.toString()));
                    mValueCache.setLength(0);
                    meetSemicolon = true;
                    lookFor(END_BRACE | KEY);
                    break;

                case Int:
                    check(VALUE);
                    appendToValueCacheWisely(mCurToken.intValue());
                    lookFor(SEMICOLON | END_BRACE | VALUE);
                    break;

                case Double:
                    check(VALUE);
                    appendToValueCacheWisely(mCurToken.doubleValue());
                    lookFor(SEMICOLON | END_BRACE | VALUE);
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
                    check(SELECTOR_CLASS | SELECTOR_ID | SELECTOR_TAG);
                    if (isLookingFor(SELECTOR_CLASS)) {
                        if (cssSelector == null) {
                            cssSelector = new ClassSelector(mCurToken.stringValue());
                            segment.mCss.newAttr(cssSelector);
                        } else {
                            cssSelector.chain(new ClassSelector(mCurToken.stringValue()));
                        }
                    } else if (isLookingFor(SELECTOR_ID)) {
                        if (cssSelector == null) {
                            cssSelector = new IdSelector(mCurToken.stringValue());
                            segment.mCss.newAttr(cssSelector);
                        } else {
                            cssSelector.chain(new IdSelector(mCurToken.stringValue()));
                        }
                    } else if (isLookingFor(SELECTOR_TAG)) {
                        if (cssSelector == null) {
                            cssSelector = new TypeSelector(mCurToken.stringValue());
                            segment.mCss.newAttr(cssSelector);
                        } else {
                            cssSelector.chain(new TypeSelector(mCurToken.stringValue()));
                        }
                    }
                    lookFor(START_BRACE | SELECTOR_START);
                    break;

                case StartAngleBracket:
                    check(SELECTOR_START);
                    scan();
                    if (mCurToken.type() == TokenType.Slash) {
                        Log.d(TAG, segment.mCss.toString());
                        return;
                    }
                    // if parse process didn't end, then there is a syntax error.
                default:
                    Log.e(TAG, "unknown token " + mCurToken.toString() + " when parsing css");
                    throw new HNSyntaxError("unknown token " + mCurToken.toString() + " when " +
                            "parsing css", lexer.line(), lexer.column());
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

        Log.d(TAG, "Css -> next is " + mCurToken.toString());
    }

    private void check(int status) throws HNSyntaxError {
        if (!isLookingFor(status)) {
            Log.e(TAG, " Looking for " + lookForToString(status) + ", but " +
                    "currently is " +
                    lookForToString(this.lookFor));
            throw new HNSyntaxError(" Looking for " + lookForToString(status) + ", but " +
                    "currently is " +
                    lookForToString(this.lookFor), lexer.line(), lexer.column());
        }
    }

    private void appendToValueCacheWisely(Object o) {
        if (mValueCache.length() == 0) {
            mValueCache.append(o);
        } else {
            mValueCache.append(' ').append(o);
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

        if ((lookFor & SELECTOR_TAG) != 0) {
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
}
