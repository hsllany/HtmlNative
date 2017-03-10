package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mozz.remoteview.reader.CodeReader;
import com.mozz.remoteview.token.Token;
import com.mozz.remoteview.token.Type;

import java.io.EOFException;

import static com.mozz.remoteview.token.Type.Id;
import static com.mozz.remoteview.token.Type.LeftAngleBracket;
import static com.mozz.remoteview.token.Type.RightAngleBracket;
import static com.mozz.remoteview.token.Type.Script;
import static com.mozz.remoteview.token.Type.Slash;
import static com.mozz.remoteview.token.Type.Template;

/**
 * @author YangTao7
 */
final class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    static boolean DEBUG = true;

    @NonNull
    private final Lexer mLexer;

    private int mLookFor;

    @Nullable
    private Token mCurToken;

    private boolean mReserved = false;

    private static final int LK_LeftArrowBracket = 1;
    private static final int LK_RightArrowBracket = 1 << 1;
    private static final int LK_ID = 1 << 2;
    private static final int LK_VALUE = 1 << 3;
    private static final int LK_SLASH = 1 << 4;
    private static final int LK_EQUAL = 1 << 5;
    private static final int LK_INT = 1 << 6;
    private static final int LK_DOUBLE = 1 << 7;
    private static final int LK_CODE = 1 << 8;
    private static final int LK_INNER = 1 << 9;
    private static final int LK_NUMBER = LK_INT | LK_DOUBLE;

    /**
     * If parser met with swallowInnerTag, the inner element of token will become the
     * attribute of the element instead of creating a new child tree.
     */
    private static final String[] sSwallowInnerTag = {HtmlTag.A, HtmlTag.B, HtmlTag.H1, HtmlTag.H2,
            HtmlTag.INPUT, HtmlTag.P};

    private static boolean isSwallowInnerTag(@NonNull String tag) {
        for (String tagToCompare : sSwallowInnerTag) {
            if (tag.equals(tagToCompare)) {
                return true;
            }
        }

        return false;
    }

    public Parser(CodeReader reader) {
        mLexer = new Lexer(reader);
    }

    @NonNull
    public RVModule process() throws RVSyntaxError {

        RVModule module = new RVModule();
        module.mRootTree = new RVDomTree(module, null, 0, 0);

        RVDomTree currentTree = module.mRootTree;

        try {
            scanFor(LeftAngleBracket);

            // Look ahead to determine whether current is script or template
            scan();

            // Script situation
            if (mCurToken.type() == Script) {
                scanFor(RightAngleBracket);

                // scan for code
                scan(true);

                if (mCurToken.type() == Id) {
                    scan();
                    processCode(mCurToken.stringValue(), module);
                }

                scanFor(LeftAngleBracket, Slash, Script, RightAngleBracket);

                //scan for <template> tag
                scanFor(LeftAngleBracket, Template);

                currentTree.mNodeName = mCurToken.stringValue();
                currentTree.mTagPair = 1;
                currentTree.mBracketPair = 1;
                processInternal(currentTree);

                scan();

                throw new RVSyntaxError("should end", mLexer.line(), mLexer.column());

            } else if (mCurToken.type() == Template) {
                currentTree.mNodeName = mCurToken.stringValue();
                currentTree.mTagPair = 1;
                currentTree.mBracketPair = 1;
                processInternal(currentTree);

                scanFor(LeftAngleBracket, Script, RightAngleBracket);
                scan(true);

                if (mCurToken.type() == Id) {
                    scan();

                    processCode(mCurToken.stringValue(), module);
                }

                scanFor(LeftAngleBracket, Slash, Script, RightAngleBracket);
                scan();
                throw new RVSyntaxError("should end", mLexer.line(), mLexer.column());


            } else {
                throw new RVSyntaxError("must init with <template> or <script>", mLexer.line(),
                        mLexer.column());
            }

        } catch (EOFException e) {
            mLexer.close();
            return module;
        }

    }

    /**
     * parse the lua code block
     *
     * @throws RVSyntaxError
     */
    private void processCode(String functionName, @NonNull RVModule module) throws RVSyntaxError {
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
                        module.putFunction(functionName, mCurToken.stringValue());
                        lookFor(LK_ID);
                        break;

                    // if meet other token, just return
                    default:
                        mReserved = true;
                        return;
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
    private void processInternal(@NonNull RVDomTree tree) throws RVSyntaxError {
        log("init to parse tree " + tree.getNodeName());
        int index = 0;

        lookFor(LK_ID | LK_RightArrowBracket | LK_SLASH);

        String attrName = null;

        boolean meetEndTag = false;

        int innerCount = 0;

        try {
            while (true) {
                scan();

                switch (mCurToken.type()) {
                    case LeftAngleBracket:

                        checkState(LK_LeftArrowBracket);

                        lookFor(LK_SLASH | LK_ID);

                        scan();

                        if (mCurToken.type() == Slash) {

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

                            if (mCurToken.type() != RightAngleBracket) {
                                throw new RVSyntaxError("View tag must be end with >", mLexer.line(),
                                        mLexer.column());
                            }

                            // here reach the end of the view tree, just return.
                            return;

                        } else if (mCurToken.type() == Id) {

                            checkState(LK_ID);

                            String tag = mCurToken.stringValue();

                            // handle the <br/> tag
                            if (HtmlTag.BR.equalsIgnoreCase(tag)) {
                                if (isSwallowInnerTag(tree.getNodeName())) {
                                    tree.appendText("\n");
                                } else {
                                    tree.last().appendText("\n");
                                }
                                scanFor(Type.Slash, Type.RightAngleBracket);
                                lookFor(LK_LeftArrowBracket | LK_INNER);

                            } else {
                                RVDomTree child = tree.addChild(tag, index++);
                                child.mTagPair = 1;
                                child.mBracketPair = 1;
                                processInternal(child);
                                lookFor(LK_LeftArrowBracket);
                            }
                        }
                        break;

                    case RightAngleBracket:
                        checkState(LK_RightArrowBracket);
                        lookFor(LK_LeftArrowBracket | LK_INNER);

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
                        if (attrName.equals(HtmlTag.ATTR_STYLE)) {
                            parseStyle(tree, mCurToken.stringValue());
                        } else {
                            tree.addAttr(attrName, mCurToken.stringValue());
                        }
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

                    case Inner:
                        checkState(LK_INNER);
                        if (isSwallowInnerTag(tree.getNodeName())) {
                            tree.appendText(mCurToken.stringValue());
                        } else {
                            RVDomTree innerChild = tree.addChild(RVDomTree.INNER_TREE_TAG, innerCount++);
                            innerChild.appendText(mCurToken.stringValue());
                        }

                        lookFor(LK_LeftArrowBracket);
                        break;
                    // for <a/> case
                    case Slash:

                        tree.mTagPair--;

                        checkState(LK_SLASH);

                        lookFor(LK_RightArrowBracket);

                        scan();

                        if (mCurToken.type() != RightAngleBracket) {
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

    static void parseStyle(@NonNull RVDomTree tree, @NonNull String styleString) {
        StringBuilder sb = new StringBuilder();
        String key = null;
        for (int i = 0; i < styleString.length(); i++) {
            char c = styleString.charAt(i);

            if (c == ';') {
                tree.addAttr(key, sb.toString());
                sb.setLength(0);
            } else if (c == ':') {
                key = sb.toString();
                sb.setLength(0);
            } else {
                if (c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\f' || c == '\b') {
                    continue;
                }
                sb.append(c);
            }
        }

        if (key != null) {
            tree.addAttr(key, sb.toString());
        }
    }

    private boolean isLookingFor(int status) {
        return (mLookFor & status) != 0;
    }

    private void lookFor(int status) {
        mLookFor |= status;
    }

    private void scan() throws EOFException, RVSyntaxError {
        if (mReserved) {
            log("re-process token ->" + mCurToken);
            mReserved = false;
            return;
        }
        if (mCurToken != null)
            mCurToken.recycle();
        mCurToken = mLexer.scan();


        log("process token ->" + mCurToken);

    }

    private void scan(boolean reserved) throws EOFException, RVSyntaxError {
        scan();
        mReserved = reserved;
    }

    private void scanFor(@NonNull Type type) throws EOFException, RVSyntaxError {
        scan();

        if (mCurToken.type() != type) {
            throw new RVSyntaxError("syntax error, should be " + type.toString() +
                    "， but current is " + mCurToken.toString(), mLexer.line(), mLexer.column());
        }
    }

    private void scanFor(@NonNull Type... types) throws EOFException, RVSyntaxError {
        for (Type type : types) {
            scanFor(type);
        }
    }

    private static void log(String msg) {
        if (DEBUG)
            System.out.println(msg);
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
