package com.mozz.htmlnative;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mozz.htmlnative.css.ClassSelector;
import com.mozz.htmlnative.css.CssSelector;
import com.mozz.htmlnative.css.IdSelector;
import com.mozz.htmlnative.css.TagSelector;
import com.mozz.htmlnative.token.Token;
import com.mozz.htmlnative.token.TokenType;

import java.io.EOFException;

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
    private static final int VALUE = 1 << 6;
    private static final int COLON = 1 << 7;
    private static final int SEMICOLON = 1 << 8;

    private static final int SELECTOR_START = SELECTOR_HASH | SELECTOR_TAG | SELECTOR_DOT;

    private int lookFor;

    @Nullable
    private Token mCurToken;

    private final Lexer lexer;

    public CssParser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void process(HNSegment segment) throws EOFException, HNSyntaxError {
        lookFor(SELECTOR_START);

        CssSelector cssSelector = null;

        String keyCache = null;

        while (true) {
            scan();

            switch (mCurToken.type()) {
                case Hash:
                    check(SELECTOR_HASH);
                    lookFor(SELECTOR_ID);
                    break;
                case Id:
                    // tag selector should be in the first position of whole if-statement
                    if (isLookingFor(SELECTOR_TAG)) {
                        if (cssSelector == null) {
                            cssSelector = new TagSelector(mCurToken.stringValue());
                            segment.mCss.newAttr(cssSelector);
                        } else {
                            cssSelector.chain(new TagSelector(mCurToken.stringValue()));
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
                    } else if (isLookingFor(VALUE)) {
                        check(VALUE);
                        segment.mCss.putAttr(cssSelector, keyCache, mCurToken.stringValue());
                        lookFor(SEMICOLON | END_BRACE);
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
                    segment.mCss.putSelector(cssSelector);
                    cssSelector = null;
                    break;

                case Semicolon:
                    check(SEMICOLON);
                    lookFor(END_BRACE | KEY);
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
                    throw new HNSyntaxError("wrong when parsing css", lexer.line(), lexer.column());
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
            throw new HNSyntaxError(" Looking for " + status + ", but " +
                    "currently is " +
                    this.lookFor, lexer.line(), lexer.column());
        }
    }


}
