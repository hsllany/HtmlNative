package com.mozz.remoteview.json.parser;

import android.util.Log;

import com.mozz.remoteview.json.parser.reader.CodeReader;
import com.mozz.remoteview.json.parser.token.Token;
import com.mozz.remoteview.json.parser.token.Type;

import java.io.EOFException;

import static com.mozz.remoteview.json.parser.token.Type.*;

public final class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    private static boolean DEBUG = false;

    private final Lexer mLexer;

    private int mLookFor;

    private Token mCurrentToken;

    private static final int LK_LeftArrowBracket = 1;
    private static final int LK_RightArrowBracket = 1 << 1;
    private static final int LK_ID = 1 << 2;
    private static final int LK_VALUE = 1 << 3;
    private static final int LK_SLASH = 1 << 4;
    private static final int LK_EQUAL = 1 << 5;
    private static final int LK_INT = 1 << 6;
    private static final int LK_DOUBLE = 1 << 7;
    private static final int LK_NUMBER = LK_INT | LK_DOUBLE;

    public Parser(CodeReader reader) {
        mLexer = new Lexer(reader);
    }

    public SyntaxTree process() throws SytaxError {

        lookFor(LK_LeftArrowBracket);


        try {
            scan();

            if (mCurrentToken.type() == LeftAngleBracket) {
                lookFor(LK_ID);

                scan();
                if (mCurrentToken.type() != Type.Id) {
                    throw new SytaxError("", mLexer.line());
                }

                SyntaxTree tree = new SyntaxTree(mCurrentToken.stringValue(), null, 0, 0);
                tree.mTagPair = 1;
                tree.mBracketPair = 1;
                processInternal(tree);

                return tree;
            } else {
                throw new SytaxError("< is need", mLexer.line());
            }

        } catch (EOFException e) {
            return null;
        }
    }

    private void processInternal(SyntaxTree tree) throws SytaxError {
        int index = 0;

        lookFor(LK_VALUE | LK_RightArrowBracket | LK_SLASH);

        String attrName = null;

        boolean meetEndTag = false;
        try {
            while (true) {
                scan();

                switch (mCurrentToken.type()) {
                    case LeftAngleBracket:

                        checkLookingFor(LK_LeftArrowBracket);

                        lookFor(LK_SLASH | LK_ID);

                        scan();

                        if (mCurrentToken.type() == Type.Slash) {

                            meetEndTag = true;

                            tree.mBracketPair++;
                            checkLookingFor(LK_SLASH);
                            scan();

                            // compare the tag string
                            if (!tree.getNodeName().equals(mCurrentToken.value())) {
                                throw new SytaxError("node is not right" + mCurrentToken.value() + ", " + tree.getNodeName(), mLexer.line());
                            }

                            scan();

                            if (mCurrentToken.type() != Type.RightAngleBracket) {
                                throw new SytaxError("must be end with >", mLexer.line());
                            }
                            return;

                        } else if (mCurrentToken.type() == Type.Id) {

                            checkLookingFor(LK_ID);

                            String tag = mCurrentToken.stringValue();

                            SyntaxTree child = tree.addChild(tag, index++);
                            child.mTagPair = 1;
                            child.mBracketPair = 1;
                            processInternal(child);
                            lookFor(LK_LeftArrowBracket);
                        }
                        break;

                    case RightAngleBracket:
                        checkLookingFor(LK_RightArrowBracket);
                        lookFor(LK_LeftArrowBracket);

                        tree.mBracketPair--;
                        if (tree.mBracketPair != 0) {
                            throw new SytaxError("<> must be in pairs, " + ", bracketPair=" + tree.mBracketPair, mLexer.line());
                        }

                        break;

                    case Id:
                        checkLookingFor(LK_ID);
                        attrName = mCurrentToken.stringValue();
                        lookFor(LK_EQUAL);
                        break;

                    case Equal:
                        checkLookingFor(LK_EQUAL);
                        if (attrName == null) {
                            throw new SytaxError("attrName is null", mLexer.line());
                        }
                        lookFor(LK_VALUE | LK_NUMBER);
                        break;

                    case Value:
                        checkLookingFor(LK_VALUE);
                        tree.addAttr(attrName, mCurrentToken.stringValue());
                        lookFor(LK_ID | LK_RightArrowBracket);
                        break;

                    case Int:
                        checkLookingFor(LK_INT);
                        tree.addAttr(attrName, mCurrentToken.intValue());
                        lookFor(LK_ID | LK_RightArrowBracket);
                        break;

                    case Double:
                        checkLookingFor(LK_DOUBLE);
                        tree.addAttr(attrName, mCurrentToken.doubleValue());
                        lookFor(LK_ID | LK_RightArrowBracket);
                        break;

                    case Slash:

                        tree.mTagPair--;
                        checkLookingFor(LK_SLASH);
                        lookFor(LK_RightArrowBracket);

                        scan();
                        if (mCurrentToken.type() != Type.RightAngleBracket) {
                            throw new SytaxError("unknown tag", mLexer.line());
                        }

                        tree.mBracketPair--;
                        if (tree.mBracketPair != 0) {
                            throw new SytaxError("<> must be in pairs, " + ", bracketPair=" + tree.mBracketPair, mLexer.line());
                        }
                        return;

                }

            }
        } catch (EOFException e) {
            if (meetEndTag) {
                throw new SytaxError("not end with </", mLexer.line());
            }
            return;
        }
    }

    private boolean isLookingFor(int status) {
        return (mLookFor & status) != 0;
    }

    private void lookFor(int status) {
        mLookFor |= status;
    }

    private void scan() throws EOFException, SytaxError {
        mCurrentToken = mLexer.scan();
    }

    private static void log(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }

    private void checkLookingFor(int status) throws SytaxError {
        if (!isLookingFor(status)) {
            throw new SytaxError("Looking for " + status, this.mLexer.line());
        }
    }
}
