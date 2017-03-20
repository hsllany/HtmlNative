package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mozz.remoteview.reader.TextReader;
import com.mozz.remoteview.script.ScriptInfo;
import com.mozz.remoteview.token.Token;
import com.mozz.remoteview.token.TokenType;

import java.io.EOFException;

import static com.mozz.remoteview.HtmlTag.isSwallowInnerTag;
import static com.mozz.remoteview.token.TokenType.EndAngleBracket;
import static com.mozz.remoteview.token.TokenType.Equal;
import static com.mozz.remoteview.token.TokenType.Head;
import static com.mozz.remoteview.token.TokenType.Html;
import static com.mozz.remoteview.token.TokenType.Id;
import static com.mozz.remoteview.token.TokenType.Inner;
import static com.mozz.remoteview.token.TokenType.Meta;
import static com.mozz.remoteview.token.TokenType.Script;
import static com.mozz.remoteview.token.TokenType.Slash;
import static com.mozz.remoteview.token.TokenType.StartAngleBracket;
import static com.mozz.remoteview.token.TokenType.Template;
import static com.mozz.remoteview.token.TokenType.Title;

/**
 * @author YangTao7
 */
final class Parser {

    private static final String TAG = "Parser";

    @NonNull
    private final Lexer mLexer;

    private int mLookFor;

    @Nullable
    private Token mCurToken;

    private boolean mReserved = false;

    private static final int LK_StartArrowBracket = 1;
    private static final int LK_EndArrowBracket = 1 << 1;

    private static final int LK_ID = 1 << 2;
    private static final int LK_VALUE = 1 << 3;
    private static final int LK_SLASH = 1 << 4;
    private static final int LK_EQUAL = 1 << 5;
    private static final int LK_INT = 1 << 6;
    private static final int LK_DOUBLE = 1 << 7;
    private static final int LK_CODE = 1 << 8;
    private static final int LK_INNER = 1 << 9;
    private static final int LK_NUMBER = LK_INT | LK_DOUBLE;


    public Parser(TextReader reader) {
        mLexer = new Lexer(reader);
    }

    public RVSegment process() throws RVSyntaxError {
        RVSegment segment = new RVSegment();
        segment.mRootTree = new RVDomTree(segment, null, 0, 0);

        try {
            scanFor(StartAngleBracket);

            scan(true);

            if (mCurToken.type() == Html) {
                scan();
                scanFor(EndAngleBracket, StartAngleBracket);
                processHtmlInside(segment);
            } else {
                processHtmlInside(segment);
            }

            scanFor(StartAngleBracket, Slash, Html, EndAngleBracket);
        } catch (EOFException e) {
            Log.d(TAG, "Reach the end of stream");

        } finally {
            mLexer.close();
            return segment;
        }
    }

    @NonNull
    private void processHtmlInside(RVSegment segment) throws RVSyntaxError, EOFException {

        RVDomTree currentTree = segment.mRootTree;

        // Look ahead to determine whether current is script or template
        scan();

        switch (mCurToken.type()) {
            case Template:
                processTemplateThenScript(currentTree, segment);
                return;

            case Head:
                processHead(segment);
                scanFor(StartAngleBracket, Template);
                processTemplateThenScript(currentTree, segment);
                return;

            default:
                throw new RVSyntaxError("must init with <template> or <script>", mLexer.line(),
                        mLexer.column());

        }
    }

    private void processTemplateThenScript(RVDomTree tree, RVSegment segment) throws
            EOFException, RVSyntaxError {
        processTemplate(tree);
        scanFor(StartAngleBracket, Script);
        processScript(segment);
        scan();
    }

    private void processScript(RVSegment segment) throws RVSyntaxError, EOFException {
        if (mCurToken.type() != Script) {
            throw new RVSyntaxError("Look for script, but " + mCurToken.toString(), mLexer.line()
                    , mLexer.column());
        }

        String attrName = null;

        lookFor(LK_ID | LK_EndArrowBracket);

        String type = null;

        while (true) {
            scan();

            switch (mCurToken.type()) {
                case EndAngleBracket: {
                    check(LK_EndArrowBracket);

                    Token scriptToken = mLexer.scanScript();
                    System.out.println("script = " + scriptToken);

                    segment.mScriptInfo = new ScriptInfo(scriptToken, type);
                    scanFor(StartAngleBracket, Slash, Script, EndAngleBracket);
                    return;
                }
                case Id:
                    check(LK_ID);
                    attrName = mCurToken.stringValue();
                    lookFor(LK_EQUAL);
                    break;

                case Equal:
                    check(LK_EQUAL);
                    lookFor(LK_VALUE);
                    break;

                case Value:
                    check(LK_VALUE);
                    if (attrName.equals("type")) {
                        type = mCurToken.stringValue();
                    }

                    lookFor(LK_EndArrowBracket | LK_ID);
                    break;
            }
        }
    }

    private void processHead(RVSegment segment) throws RVSyntaxError, EOFException {
        if (mCurToken.type() != TokenType.Head) {
            throw new RVSyntaxError("Look for \"head\", but " + mCurToken.toString(), mLexer.line
                    (), mLexer.column());
        }

        while (true) {
            scan();

            if (mCurToken.type() == Slash) {
                scanFor(EndAngleBracket);
                return;
            } else if (mCurToken.type() == Title) {
                processTitle(segment);
            } else if (mCurToken.type() == Meta) {
                processMeta(segment);
            } else if (mCurToken.type() == StartAngleBracket) {
                scan(true);

                if (mCurToken.type() == Slash) {
                    scanFor(Slash, Head, EndAngleBracket);
                    return;
                }
            }
        }
    }

    private void processTitle(RVSegment segment) throws RVSyntaxError, EOFException {
        if (mCurToken.type() != Title) {
            throw new RVSyntaxError("Look for head, but " + mCurToken.toString(), mLexer.line(),
                    mLexer.column());
        }

        scanFor(EndAngleBracket);
        scanFor(Inner);

        String title = mCurToken.stringValue();
        segment.setTitle(title);

        scanFor(StartAngleBracket, Slash, Title, EndAngleBracket);
    }

    private void processMeta(RVSegment segment) throws RVSyntaxError, EOFException {
        if (mCurToken.type() != Meta) {
            throw new RVSyntaxError("Look for meta, but " + mCurToken.toString(), mLexer.line(),
                    mLexer.column());
        }

        Meta meta = new Meta();

        String idCache = null;

        lookFor(LK_ID | LK_SLASH);

        while (true) {
            scan();

            switch (mCurToken.type()) {
                case Id:
                    check(LK_ID);
                    idCache = mCurToken.stringValue();
                    scanFor(Equal);
                    lookFor(LK_VALUE);
                    break;

                case Value:
                    check(LK_VALUE);
                    if (com.mozz.remoteview.Meta.ID_NAME.equalsIgnoreCase(idCache)) {
                        meta.name = mCurToken.stringValue();
                    } else if (com.mozz.remoteview.Meta.ID_CONTENT.equals(idCache)) {
                        meta.content = mCurToken.stringValue();
                    }

                    lookFor(LK_ID | LK_SLASH);
                    break;
                case Slash:
                    segment.putMeta(meta);
                    check(LK_SLASH);
                    scanFor(EndAngleBracket);
                    return;

                default:
                    throw new RVSyntaxError("Unknown token " + mCurToken.toString() + " when " +
                            "parsing <meta>" + mCurToken.toString(), mLexer.line(), mLexer.column
                            ());
            }
        }


    }

    private void processTemplate(RVDomTree tree) throws RVSyntaxError {
        if (mCurToken.type() != Template) {
            throw new RVSyntaxError("Look for Template, but " + mCurToken.toString(), mLexer.line
                    (), mLexer.column());
        }

        tree.mNodeName = mCurToken.stringValue();
        tree.mTagPair = 1;
        tree.mBracketPair = 1;
        processInternal(tree);
    }

    private void processInternal(@NonNull RVDomTree tree) throws RVSyntaxError {
        processInternal(tree, tree);
    }

    /**
     * parse the tree recursively
     *
     * @throws RVSyntaxError
     */
    private void processInternal(@NonNull RVDomTree tree, @NonNull ParseCallback callback) throws
            RVSyntaxError {
        EventLog.writeEvent(EventLog.TAG_PARSER, "init to parse tree " + tree.getNodeName());
        int index = 0;

        lookFor(LK_ID | LK_EndArrowBracket | LK_SLASH);

        String attrName = null;

        boolean meetEndTag = false;

        int innerCount = 0;

        callback.onStartParse();

        try {
            while (true) {
                scan();

                switch (mCurToken.type()) {
                    case StartAngleBracket:

                        check(LK_StartArrowBracket);

                        lookFor(LK_SLASH | LK_ID);

                        scan();

                        if (mCurToken.type() == Slash) {

                            meetEndTag = true;

                            tree.mBracketPair++;
                            check(LK_SLASH);
                            scan();

                            // compare the tag string with tree.nodeName
                            if (!tree.getNodeName().equals(mCurToken.value())) {
                                throw new RVSyntaxError("View tag should be in pairs, current " +
                                        "is<" + tree.getNodeName() + "></" + mCurToken.value() +
                                        ">", mLexer.line(), mLexer.column());
                            }

                            scan();

                            if (mCurToken.type() != EndAngleBracket) {
                                throw new RVSyntaxError("View tag must be end with >", mLexer
                                        .line(), mLexer.column());
                            }

                            // here reach the end of the view tree, just return.
                            callback.onLeaveParse();
                            return;

                        } else if (mCurToken.type() == Id) {

                            check(LK_ID);

                            String tag = mCurToken.stringValue();

                            // handle the <br/> tag
                            if (HtmlTag.BR.equalsIgnoreCase(tag)) {
                                if (isSwallowInnerTag(tree.getNodeName())) {
                                    tree.appendText("\n");
                                } else {
                                    tree.last().appendText("\n");
                                }
                                scanFor(TokenType.Slash, TokenType.EndAngleBracket);
                                lookFor(LK_StartArrowBracket | LK_INNER);

                            } else {
                                RVDomTree child = new RVDomTree(tree, tag, index++);
                                tree.addChild(child);
                                child.mTagPair = 1;
                                child.mBracketPair = 1;
                                processInternal(child);
                                lookFor(LK_StartArrowBracket);
                            }
                        }
                        break;

                    case EndAngleBracket:
                        check(LK_EndArrowBracket);
                        lookFor(LK_StartArrowBracket | LK_INNER);

                        tree.mBracketPair--;
                        if (tree.mBracketPair != 0) {
                            throw new RVSyntaxError("< > must be in pairs, " + ", current bracket" +
                                    " pair is " + tree.mBracketPair, mLexer.line(), mLexer.column
                                    ());
                        }

                        break;

                    case Id:
                        check(LK_ID);
                        attrName = mCurToken.stringValue();
                        lookFor(LK_EQUAL);
                        break;

                    case Equal:
                        check(LK_EQUAL);
                        if (attrName == null) {
                            throw new RVSyntaxError("attrName is null, please check the state",
                                    mLexer.line(), mLexer.column());
                        }
                        lookFor(LK_VALUE | LK_NUMBER);
                        break;

                    case Value:
                        check(LK_VALUE);
                        if (attrName.equals(HtmlTag.ATTR_STYLE)) {
                            parseStyle(tree, mCurToken.stringValue());
                        } else {
                            tree.addAttr(attrName, mCurToken.stringValue());
                        }
                        lookFor(LK_ID | LK_EndArrowBracket | LK_SLASH);
                        break;

                    case Int:
                        check(LK_INT);
                        tree.addAttr(attrName, mCurToken.intValue());
                        lookFor(LK_ID | LK_EndArrowBracket);
                        break;

                    case Double:
                        check(LK_DOUBLE);
                        tree.addAttr(attrName, mCurToken.doubleValue());
                        lookFor(LK_ID | LK_EndArrowBracket);
                        break;

                    case Inner:
                        check(LK_INNER);
                        if (isSwallowInnerTag(tree.getNodeName())) {
                            tree.appendText(mCurToken.stringValue());
                        } else {
                            RVDomTree innerChild = new RVDomTree(tree, RVDomTree.INNER_TREE_TAG,
                                    innerCount++);
                            tree.addChild(innerChild);
                            innerChild.appendText(mCurToken.stringValue());
                        }

                        lookFor(LK_StartArrowBracket);
                        break;
                    // for <a/> case
                    case Slash:

                        tree.mTagPair--;

                        check(LK_SLASH);

                        lookFor(LK_EndArrowBracket);

                        scan();

                        if (mCurToken.type() != EndAngleBracket) {
                            throw new RVSyntaxError("unknown state, slash should be followed by " +
                                    ">, " +
                                    "but currently " + mCurToken.type(), mLexer.line(), mLexer
                                    .column());
                        }

                        tree.mBracketPair--;
                        if (tree.mBracketPair != 0) {
                            throw new RVSyntaxError("< > must be in pairs, " + ", current bracket" +
                                    " pair is " + tree.mBracketPair, mLexer.line(), mLexer.column
                                    ());
                        }
                        callback.onLeaveParse();
                        return;

                    default:
                        throw new RVSyntaxError("unknown token " + mCurToken.toString(), mLexer
                                .line(), mLexer.column());


                }

            }
        } catch (EOFException e) {
            if (meetEndTag) {
                throw new RVSyntaxError("View Tag should ends with </", mLexer.line(), mLexer
                        .column());
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
        mLookFor = 0;
        mLookFor |= status;
    }

    private void scan() throws EOFException, RVSyntaxError {
        if (mReserved) {
            EventLog.writeEvent(EventLog.TAG_PARSER, "Reprocess token ->" + mCurToken);
            mReserved = false;
            return;
        }
        if (mCurToken != null) {
            mCurToken.recycle();
        }
        mCurToken = mLexer.scan();


        EventLog.writeEvent(EventLog.TAG_PARSER, "Process token ->" + mCurToken);

    }

    private void scan(boolean reserved) throws EOFException, RVSyntaxError {
        scan();
        mReserved = reserved;
    }

    private void scanFor(@NonNull TokenType tokenType) throws EOFException, RVSyntaxError {
        scan();

        if (mCurToken.type() != tokenType) {
            throw new RVSyntaxError("syntax error, should be " + tokenType.toString() +
                    "， but current is " + mCurToken.toString(), mLexer.line(), mLexer.column());
        }
    }

    private void scanFor(@NonNull TokenType... tokenTypes) throws EOFException, RVSyntaxError {
        for (TokenType tokenType : tokenTypes) {
            scanFor(tokenType);
        }
    }

    private void check(int status) throws RVSyntaxError {
        if (!isLookingFor(status)) {
            throw new RVSyntaxError(" Looking for " + lookForToString(status) + ", but " +
                    "currently is " +
                    lookForToString(mLookFor), mLexer.line(), mLexer.column());
        }
    }

    private static String lookForToString(int lookFor) {
        StringBuilder sb = new StringBuilder("[");

        if ((lookFor & LK_EndArrowBracket) != 0) {
            sb.append("> ");
        }

        if ((lookFor & LK_StartArrowBracket) != 0) {
            sb.append("< ");
        }

        if ((lookFor & LK_ID) != 0) {
            sb.append("id ");
        }

        if ((lookFor & LK_VALUE) != 0) {
            sb.append("value ");
        }

        if ((lookFor & LK_SLASH) != 0) {
            sb.append("/ ");
        }

        if ((lookFor & LK_EQUAL) != 0) {
            sb.append("= ");
        }

        if ((lookFor & LK_NUMBER) != 0) {
            sb.append("number ");
        }

        if ((lookFor & LK_CODE) != 0) {
            sb.append("code ");
        }

        if ((lookFor & LK_INNER) != 0) {
            sb.append("innerElement ");
        }

        sb.append("]");

        return sb.toString();

    }

    interface ParseCallback {
        void onStartParse();

        void onLeaveParse();
    }
}
