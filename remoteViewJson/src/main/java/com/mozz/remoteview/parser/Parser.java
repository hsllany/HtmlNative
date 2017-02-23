package com.mozz.remoteview.parser;

import android.util.Log;

import com.mozz.remoteview.parser.reader.CodeReader;
import com.mozz.remoteview.parser.token.Token;
import com.mozz.remoteview.parser.token.Type;

import java.io.EOFException;

import static com.mozz.remoteview.parser.token.Type.Id;
import static com.mozz.remoteview.parser.token.Type.LeftAngleBracket;

public final class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    static boolean DEBUG = false;

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
    private static final int LK_CODE = 1 << 8;
    private static final int LK_NUMBER = LK_INT | LK_DOUBLE;

    public Parser(CodeReader reader) {
        mLexer = new Lexer(reader);
    }

    public RVContext process() throws SyntaxError {

        lookFor(LK_LeftArrowBracket | LK_ID);

        RVContext rvContext = new RVContext();
        rvContext.mRootTree = new RVDomTree(rvContext, null, 0, 0);
        rvContext.mFunctionTable = new FunctionTable();

        try {
            scan();

            if (mCurrentToken.type() == LeftAngleBracket) {
                lookFor(LK_ID);

                scan();
                if (mCurrentToken.type() != Type.Id) {
                    throw new SyntaxError("", mLexer.line());
                }

                rvContext.mRootTree.mNodeName = mCurrentToken.stringValue();

                rvContext.mRootTree.mTagPair = 1;
                rvContext.mRootTree.mBracketPair = 1;

                // scan the view tree first
                processInternal(rvContext.mRootTree);

                // scan the related code then
                scan();
                if (mCurrentToken.type() == Id) {
                    processCode(mCurrentToken.stringValue(), rvContext.mFunctionTable, false);
                }

            } else if (mCurrentToken.type() == Id) {

                processCode(mCurrentToken.stringValue(), rvContext.mFunctionTable, true);

                if (mCurrentToken.type() != LeftAngleBracket) {
                    throw new SyntaxError("unknown state " + mCurrentToken, mLexer.line());
                }

                //scan for the tree node name
                scan();
                if (mCurrentToken.type() != Type.Id) {
                    throw new SyntaxError("", mLexer.line());
                }

                rvContext.mRootTree.mNodeName = mCurrentToken.stringValue();
                rvContext.mRootTree.mTagPair = 1;
                rvContext.mRootTree.mBracketPair = 1;

                processInternal(rvContext.mRootTree);

                // scan the related code then, there may be another code block here
                scan();
                if (mCurrentToken.type() == Id) {
                    processCode(mCurrentToken.stringValue(), rvContext.mFunctionTable, false);
                }


            } else {
                throw new SyntaxError("< is need", mLexer.line());
            }

        } catch (EOFException e) {
            return rvContext;
        }

        return rvContext;
    }

    private void processCode(String functionName, FunctionTable functionTable, boolean positionStart) throws SyntaxError {
        lookFor(LK_CODE);
        try {
            while (true) {
                scan();

                switch (mCurrentToken.type()) {
                    case Id:
                        checkLookingFor(LK_ID);
                        functionName = mCurrentToken.stringValue();
                        lookFor(LK_CODE);
                        break;
                    case Code:
                        checkLookingFor(LK_CODE);
                        functionTable.putFunction(functionName, mCurrentToken.stringValue());
                        lookFor(LK_ID);
                        break;

                    // if meet other token, just return
                    default:
                        if (positionStart)
                            return;
                        else
                            throw new SyntaxError("reach the end of the script", mLexer.line());
                }
            }
        } catch (EOFException e) {
            return;
        }
    }

    private void processInternal(RVDomTree tree) throws SyntaxError {
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

                            // compare the tag string with tree.nodeName
                            if (!tree.getNodeName().equals(mCurrentToken.value())) {
                                throw new SyntaxError("node is not right" + mCurrentToken.value() + ", " + tree.getNodeName(), mLexer.line());
                            }

                            scan();

                            if (mCurrentToken.type() != Type.RightAngleBracket) {
                                throw new SyntaxError("must be end with >", mLexer.line());
                            }

                            // here reach the end of the view tree, just return.
                            return;

                        } else if (mCurrentToken.type() == Type.Id) {

                            checkLookingFor(LK_ID);

                            String tag = mCurrentToken.stringValue();

                            RVDomTree child = tree.addChild(tag, index++);
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
                            throw new SyntaxError("<> must be in pairs, " + ", bracketPair=" + tree.mBracketPair, mLexer.line());
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
                            throw new SyntaxError("attrName is null", mLexer.line());
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

                    // for <a/> case
                    case Slash:

                        tree.mTagPair--;
                        checkLookingFor(LK_SLASH);
                        lookFor(LK_RightArrowBracket);

                        scan();

                        if (mCurrentToken.type() != Type.RightAngleBracket) {
                            throw new SyntaxError("unknown tag", mLexer.line());
                        }

                        tree.mBracketPair--;
                        if (tree.mBracketPair != 0) {
                            throw new SyntaxError("<> must be in pairs, " + ", bracketPair=" + tree.mBracketPair, mLexer.line());
                        }
                        return;

                    default:
                        throw new SyntaxError("unknown token", mLexer.line());


                }

            }
        } catch (EOFException e) {
            if (meetEndTag) {
                throw new SyntaxError("not end with </", mLexer.line());
            }
        }
    }

    private boolean isLookingFor(int status) {
        return (mLookFor & status) != 0;
    }

    private void lookFor(int status) {
        mLookFor |= status;
    }

    private void scan() throws EOFException, SyntaxError {
        mCurrentToken = mLexer.scan();
    }

    private static void log(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }

    private void checkLookingFor(int status) throws SyntaxError {
        if (!isLookingFor(status)) {
            throw new SyntaxError("Looking for " + status, this.mLexer.line());
        }
    }
}
