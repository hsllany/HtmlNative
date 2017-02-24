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

    private Token mCurToken;

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

    public RVModule process() throws RVSyntaxError {

        lookFor(LK_LeftArrowBracket | LK_ID);

        RVModule rvModule = new RVModule();
        rvModule.mRootTree = new RVDomTree(rvModule, null, 0, 0);
        rvModule.mFunctionTable = new FunctionTable();

        try {
            scan();

            if (mCurToken.type() == LeftAngleBracket) {
                lookFor(LK_ID);

                scan();

                // Id Token should follow <
                if (mCurToken.type() != Type.Id) {
                    throw new RVSyntaxError("unknown type of token " + mCurToken.type() +
                            ", < should be followed by view name", mLexer.line(), mLexer.column());
                }

                rvModule.mRootTree.mNodeName = mCurToken.stringValue();

                rvModule.mRootTree.mTagPair = 1;
                rvModule.mRootTree.mBracketPair = 1;

                // scan the view tree first
                processInternal(rvModule.mRootTree);

                // scan the related code then
                scan();
                if (mCurToken.type() == Id) {
                    processCode(mCurToken.stringValue(), rvModule.mFunctionTable, false);
                }

            } else if (mCurToken.type() == Id) {

                // handle the case that code appear first
                processCode(mCurToken.stringValue(), rvModule.mFunctionTable, true);

                // Id token should follow <
                if (mCurToken.type() != LeftAngleBracket) {
                    throw new RVSyntaxError("unknown type of token " + mCurToken.type() +
                            ".", mLexer.line(), mLexer.column());
                }

                //scan for the tree node name
                scan();
                if (mCurToken.type() != Type.Id) {
                    throw new RVSyntaxError("unknown type of token " + mCurToken.type() +
                            ", < should be followed by view name", mLexer.line(), mLexer.column());
                }

                rvModule.mRootTree.mNodeName = mCurToken.stringValue();
                rvModule.mRootTree.mTagPair = 1;
                rvModule.mRootTree.mBracketPair = 1;

                processInternal(rvModule.mRootTree);

                // scan the related code then, there may be another code block here
                scan();
                if (mCurToken.type() == Id) {
                    processCode(mCurToken.stringValue(), rvModule.mFunctionTable, false);
                }


            } else {
                mLexer.close();
                throw new RVSyntaxError("LuaV should start with view or code block", mLexer.line(),
                        mLexer.column());

            }

        } catch (EOFException e) {
            mLexer.close();
            return rvModule;
        }

        throw new RVSyntaxError("LuaV should start with view or code block", mLexer.line(),
                mLexer.column());
    }

    /**
     * parse the lua code block
     *
     * @throws RVSyntaxError
     */
    private void processCode(String functionName, FunctionTable functionTable,
                             boolean positionStart) throws RVSyntaxError {
        lookFor(LK_CODE);
        try {
            while (true) {
                scan();

                switch (mCurToken.type()) {
                    case Id:
                        checkState(LK_ID);
                        functionName = mCurToken.stringValue();
                        lookFor(LK_CODE);
                        break;
                    case Code:
                        checkState(LK_CODE);
                        functionTable.putFunction(functionName, mCurToken.stringValue());
                        lookFor(LK_ID);
                        break;

                    // if meet other token, just return
                    default:
                        if (positionStart)
                            return;
                        else
                            throw new EOFException();
                }
            }
        } catch (EOFException e) {

        }
    }

    /**
     * parse the tree recursively
     *
     * @throws RVSyntaxError
     */
    private void processInternal(RVDomTree tree) throws RVSyntaxError {
        int index = 0;

        lookFor(LK_VALUE | LK_RightArrowBracket | LK_SLASH);

        String attrName = null;

        boolean meetEndTag = false;
        try {
            while (true) {
                scan();

                switch (mCurToken.type()) {
                    case LeftAngleBracket:

                        checkState(LK_LeftArrowBracket);

                        lookFor(LK_SLASH | LK_ID);

                        scan();

                        if (mCurToken.type() == Type.Slash) {

                            meetEndTag = true;

                            tree.mBracketPair++;
                            checkState(LK_SLASH);
                            scan();

                            // compare the tag string with tree.nodeName
                            if (!tree.getNodeName().equals(mCurToken.value())) {
                                throw new RVSyntaxError("View tag should be in pairs, current is<"
                                        + tree.getNodeName() + "></" + mCurToken.value() + ">",
                                        mLexer.line(), mLexer.column());
                            }

                            scan();

                            if (mCurToken.type() != Type.RightAngleBracket) {
                                throw new RVSyntaxError("View tag must be end with >", mLexer.line(),
                                        mLexer.column());
                            }

                            // here reach the end of the view tree, just return.
                            return;

                        } else if (mCurToken.type() == Type.Id) {

                            checkState(LK_ID);

                            String tag = mCurToken.stringValue();

                            RVDomTree child = tree.addChild(tag, index++);
                            child.mTagPair = 1;
                            child.mBracketPair = 1;
                            processInternal(child);
                            lookFor(LK_LeftArrowBracket);
                        }
                        break;

                    case RightAngleBracket:
                        checkState(LK_RightArrowBracket);
                        lookFor(LK_LeftArrowBracket);

                        tree.mBracketPair--;
                        if (tree.mBracketPair != 0) {
                            throw new RVSyntaxError("< > must be in pairs, "
                                    + ", current bracket pair is " + tree.mBracketPair,
                                    mLexer.line(), mLexer.column());
                        }

                        break;

                    case Id:
                        checkState(LK_ID);
                        attrName = mCurToken.stringValue();
                        lookFor(LK_EQUAL);
                        break;

                    case Equal:
                        checkState(LK_EQUAL);
                        if (attrName == null) {
                            throw new RVSyntaxError("attrName is null, please check the state",
                                    mLexer.line(), mLexer.column());
                        }
                        lookFor(LK_VALUE | LK_NUMBER);
                        break;

                    case Value:
                        checkState(LK_VALUE);
                        tree.addAttr(attrName, mCurToken.stringValue());
                        lookFor(LK_ID | LK_RightArrowBracket);
                        break;

                    case Int:
                        checkState(LK_INT);
                        tree.addAttr(attrName, mCurToken.intValue());
                        lookFor(LK_ID | LK_RightArrowBracket);
                        break;

                    case Double:
                        checkState(LK_DOUBLE);
                        tree.addAttr(attrName, mCurToken.doubleValue());
                        lookFor(LK_ID | LK_RightArrowBracket);
                        break;

                    // for <a/> case
                    case Slash:

                        tree.mTagPair--;

                        checkState(LK_SLASH);

                        lookFor(LK_RightArrowBracket);

                        scan();

                        if (mCurToken.type() != Type.RightAngleBracket) {
                            throw new RVSyntaxError("unknown state, slash should be followed by >, " +
                                    "but currently " + mCurToken.type(), mLexer.line(),
                                    mLexer.column());
                        }

                        tree.mBracketPair--;
                        if (tree.mBracketPair != 0) {
                            throw new RVSyntaxError("< > must be in pairs, "
                                    + ", current bracket pair is " + tree.mBracketPair,
                                    mLexer.line(), mLexer.column());
                        }
                        return;

                    default:
                        throw new RVSyntaxError("unknown token " + mCurToken.toString(),
                                mLexer.line(), mLexer.column());


                }

            }
        } catch (EOFException e) {
            if (meetEndTag) {
                throw new RVSyntaxError("View Tag should ends with </", mLexer.line(), mLexer.column());
            }
        }
    }

    private boolean isLookingFor(int status) {
        return (mLookFor & status) != 0;
    }

    private void lookFor(int status) {
        mLookFor |= status;
    }

    private void scan() throws EOFException, RVSyntaxError {
        if (mCurToken != null)
            mCurToken.recycle();
        mCurToken = mLexer.scan();
    }

    private static void log(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }

    private void checkState(int status) throws RVSyntaxError {
        if (!isLookingFor(status)) {
            throw new RVSyntaxError(" Looking for " + status + ", but currently is " + mLookFor
                    , mLexer.line(), mLexer.column());
        }
    }
}
