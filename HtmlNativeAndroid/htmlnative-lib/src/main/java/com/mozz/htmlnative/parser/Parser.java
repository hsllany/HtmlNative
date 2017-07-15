package com.mozz.htmlnative.parser;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSegment;
import com.mozz.htmlnative.HtmlTag;
import com.mozz.htmlnative.Tracker;
import com.mozz.htmlnative.css.Styles;
import com.mozz.htmlnative.dom.HNDomTree;
import com.mozz.htmlnative.dom.Meta;
import com.mozz.htmlnative.exception.HNSyntaxError;
import com.mozz.htmlnative.parser.syntaxexc.ErrorStack;
import com.mozz.htmlnative.parser.syntaxexc.SyntaxErrorHandler;
import com.mozz.htmlnative.parser.syntaxexc.SyntaxExceptionSource;
import com.mozz.htmlnative.parser.token.Token;
import com.mozz.htmlnative.parser.token.TokenType;
import com.mozz.htmlnative.reader.TextReader;
import com.mozz.htmlnative.script.ScriptInfo;
import com.mozz.htmlnative.utils.ParametersUtils;

import java.io.EOFException;
import java.util.HashMap;
import java.util.Map;

import static com.mozz.htmlnative.HNEnvironment.PERFORMANCE_TAG;
import static com.mozz.htmlnative.HtmlTag.isSwallowInnerTag;
import static com.mozz.htmlnative.parser.token.TokenType.EndAngleBracket;
import static com.mozz.htmlnative.parser.token.TokenType.Equal;
import static com.mozz.htmlnative.parser.token.TokenType.Exclamation;
import static com.mozz.htmlnative.parser.token.TokenType.Head;
import static com.mozz.htmlnative.parser.token.TokenType.Html;
import static com.mozz.htmlnative.parser.token.TokenType.Id;
import static com.mozz.htmlnative.parser.token.TokenType.Inner;
import static com.mozz.htmlnative.parser.token.TokenType.Meta;
import static com.mozz.htmlnative.parser.token.TokenType.Script;
import static com.mozz.htmlnative.parser.token.TokenType.Slash;
import static com.mozz.htmlnative.parser.token.TokenType.StartAngleBracket;
import static com.mozz.htmlnative.parser.token.TokenType.Style;
import static com.mozz.htmlnative.parser.token.TokenType.Template;
import static com.mozz.htmlnative.parser.token.TokenType.Title;

/**
 * @author YangTao7
 */
public final class Parser implements SyntaxExceptionSource {

    private static final String ID = "id";
    private static final String CLAZZ = "class";
    private static final String TAG = Parser.class.getSimpleName();

    @NonNull
    private final Lexer mLexer;

    /**
     * To handle the css part
     */
    private final CssParser mCssParser;

    private int mLookFor;

    @NonNull
    private Token mCurToken;

    private boolean mReserved = false;

    private Map<String, Object> mStyleCache = new HashMap<>();

    private Tracker mTracker;

    private SyntaxErrorHandler mSyntaxErrorHandler;

    private long mMarkedLine = -1;
    private long mMarkedColumn = -1;

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
        ErrorStack stack = new ErrorStack();
        mSyntaxErrorHandler = new SyntaxErrorHandler(stack, this);
        mLexer = new Lexer(reader, mSyntaxErrorHandler.newChildHandler());
        mCssParser = new CssParser(mLexer, this, mSyntaxErrorHandler.newChildHandler());

        mTracker = new Tracker();
    }

    public HNSegment process() throws HNSyntaxError {

        long processStartTime = SystemClock.currentThreadTimeMillis();

        HNSegment segment = new HNSegment();
        segment.setDom(new HNDomTree(segment.getInlineStyles(), null, 0, 0));

        try {
            scanFor(StartAngleBracket);

            scan(true);

            /*
             * skip the HTML version information. see https://www.w3.org/TR/html4/struct/global
             * .html#h-7.2
             */
            if (mCurToken.type() == Exclamation) {
                mLexer.skipUntil('>');
                // consume the reserved
                scan();
                scanFor(EndAngleBracket);
                scanFor(StartAngleBracket);
                scan(true);
            }

            if (mCurToken.type() == Html) {
                scan();
                scanFor(EndAngleBracket, StartAngleBracket);
                processHtmlInside(segment);
            } else {
                processHtmlInside(segment);
            }

            scanFor(StartAngleBracket, Slash, Html, EndAngleBracket);
        } catch (EOFException ignored) {
            Log.w(TAG, "Reach the end of file!");
        } finally {
            mLexer.close();
            mTracker.record("Parse Css + Html", SystemClock.currentThreadTimeMillis() -
                    processStartTime);
            Log.i(PERFORMANCE_TAG, mTracker.dump());
            if (mSyntaxErrorHandler.hasError()) {
                Log.e(TAG, mSyntaxErrorHandler.forceDump());
            }
            return segment;
        }
    }

    private void processHtmlInside(HNSegment segment) throws HNSyntaxError, EOFException {

        HNDomTree currentTree = segment.getDom();

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
                mSyntaxErrorHandler.throwException("must init with <template> or <script>");

        }
    }

    private void processTemplateThenScript(HNDomTree tree, HNSegment segment) throws
            EOFException, HNSyntaxError {
        processTemplate(tree);
        scanFor(StartAngleBracket);
        scan(true);
        if (mCurToken.type() == Script) {
            processScript(segment);
        } else {
            scanFor(Slash, Html, EndAngleBracket);
        }
        scan();
    }

    private void processScript(HNSegment segment) throws HNSyntaxError, EOFException {
        if (mCurToken.type() != Script) {
            mSyntaxErrorHandler.throwException("Look for script, but " + mCurToken.toString());
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
                    if (scriptToken.type() != TokenType.ScriptCode) {
                        mSyntaxErrorHandler.throwException("Expect code, but meet " + scriptToken
                                .type().toString());
                    }

                    String[] mimeType = ParseHelper.parseMimeType(type);
                    if (mimeType != null && mimeType[1] != null) {

                        String typeName = mimeType[1];
                        segment.setScriptInfo(ScriptInfo.newScript(scriptToken, typeName));
                    } else {
                        mSyntaxErrorHandler.throwException("unknown script type " + type);
                    }
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
                    if (attrName != null && attrName.equals("type")) {
                        type = mCurToken.stringValue();
                    }

                    lookFor(LK_EndArrowBracket | LK_ID);
                    break;
            }
        }
    }

    private void processHead(HNSegment segment) throws HNSyntaxError, EOFException {
        if (mCurToken.type() != TokenType.Head) {
            mSyntaxErrorHandler.throwException("Look for \"head\", but " + mCurToken.toString());
        }

        while (true) {
            scan();

            if (mCurToken.type() == Slash) {
                scanFor(EndAngleBracket);
                return;
            } else if (mCurToken.type() == Title) {
                processTitle(segment);

            } else if (mCurToken.type() == Style) {

                processStyle(segment);
                scanFor(Style, EndAngleBracket);
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

    private void processStyle(HNSegment segment) throws EOFException, HNSyntaxError {
        long timeStart = SystemClock.currentThreadTimeMillis();

        // Ignore the element that is written in <style> tag
        while (true) {
            scan();

            switch (mCurToken.type()) {
                case Id:
                case Value:
                case Equal:
                case Int:
                case Double:
                    continue;
                case EndAngleBracket:
                    break;

                default:
                    mSyntaxErrorHandler.throwException("unknown " + mCurToken.toString() + " " +
                            "token in " + "<style>");
            }

            if (mCurToken.type() == EndAngleBracket) {
                break;
            }
        }
        try {
            mCssParser.process(segment);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTracker.record("Parse Css", SystemClock.currentThreadTimeMillis() - timeStart);
    }

    private void processTitle(HNSegment segment) throws HNSyntaxError, EOFException {
        if (mCurToken.type() != Title) {
            mSyntaxErrorHandler.throwException("Look for head, but " + mCurToken.toString());
        }

        scanFor(EndAngleBracket);
        scanFor(Inner);

        String title = mCurToken.stringValue();
        segment.getHead().setTitle(title);

        scanFor(StartAngleBracket, Slash, Title, EndAngleBracket);
    }

    private void processMeta(HNSegment segment) throws HNSyntaxError, EOFException {
        if (mCurToken.type() != Meta) {
            mSyntaxErrorHandler.throwException("Look for meta, but " + mCurToken.toString());
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
                    if (com.mozz.htmlnative.dom.Meta.ID_NAME.equalsIgnoreCase(idCache)) {
                        meta.setName(mCurToken.stringValue());
                    } else if (com.mozz.htmlnative.dom.Meta.ID_CONTENT.equals(idCache)) {
                        meta.setName(mCurToken.stringValue());
                    }

                    lookFor(LK_ID | LK_SLASH);
                    break;
                case Slash:
                    segment.getHead().putMeta(meta);
                    check(LK_SLASH);
                    scanFor(EndAngleBracket);
                    return;

                default:
                    mSyntaxErrorHandler.throwException("Unknown token " + mCurToken.toString() +
                            " when " + "parsing <meta>" + mCurToken.toString());
            }
        }


    }

    private void processTemplate(HNDomTree tree) throws HNSyntaxError, EOFException {

        long timeStart = SystemClock.currentThreadTimeMillis();

        if (mCurToken.type() != Template) {
            mSyntaxErrorHandler.throwException("Look for Template, but " + mCurToken.toString());
        }

        tree.setType(mCurToken.stringValue());
        processInternal(tree);

        mTracker.record("Parse Html", SystemClock.currentThreadTimeMillis() - timeStart);
    }

    private void processInternal(@NonNull HNDomTree tree) throws HNSyntaxError, EOFException {
        processInternal(tree, tree);
    }

    /**
     * parse the tree recursively
     */
    private void processInternal(@NonNull HNDomTree tree, @NonNull ParseCallback callback) throws
            HNSyntaxError, EOFException {
        HNLog.d(HNLog.PARSER, "init to parse tree " + tree.getType());
        int index = 0;

        int bracketPair = 1;

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
                            bracketPair++;
                            check(LK_SLASH);
                            scan();

                            // compare the tag string with tree.nodeName
                            if (!tree.getType().equals(mCurToken.value())) {
                                mSyntaxErrorHandler.throwException("View tag should be in pairs, " +
                                        "" + "" + "" + "" + "" + "" + "" + "current is<" + tree
                                        .getType() + "></" + mCurToken.value() + ">");
                            }

                            scan();

                            if (mCurToken.type() != EndAngleBracket) {
                                mSyntaxErrorHandler.throwException("View tag must be end with >",
                                        mCurToken.getLine(), mCurToken.getColumn());
                            }

                            bracketPair--;
                            if (bracketPair != 0) {
                                mSyntaxErrorHandler.throwException("< > must be in pairs, " +
                                        "current bracket pair is " + bracketPair, mCurToken
                                        .getLine(), mCurToken.getColumn());
                            }

                            // here reach the end of the view tree, just return.
                            callback.onLeaveParse();
                            return;

                        } else if (mCurToken.type() == Id || mCurToken.type() == Script) {
                            // "mCurToken.type() == Script" is to handle the <script> inside <body>

                            check(LK_ID);

                            String tag = mCurToken.stringValue();

                            // handle the <br/> tag
                            if (HtmlTag.BR.equalsIgnoreCase(tag)) {
                                if (isSwallowInnerTag(tree.getType())) {
                                    tree.appendText("\n");
                                } else {
                                    tree.last().appendText("\n");
                                }
                                scanFor(TokenType.Slash, TokenType.EndAngleBracket);
                                lookFor(LK_StartArrowBracket | LK_INNER);

                            } else {
                                HNDomTree child = new HNDomTree(tree, tag, index++);
                                tree.addChild(child);
                                processInternal(child);
                                lookFor(LK_StartArrowBracket);
                            }
                        }
                        break;

                    case EndAngleBracket:
                        check(LK_EndArrowBracket);
                        lookFor(LK_StartArrowBracket | LK_INNER);

                        bracketPair--;

                        break;

                    case Id:
                    case Style:
                        check(LK_ID);
                        attrName = mCurToken.stringValue();
                        lookFor(LK_EQUAL);
                        break;

                    case Equal:
                        check(LK_EQUAL);
                        if (attrName == null) {
                            mSyntaxErrorHandler.throwException("attrName is null, please check "
                                    + "the state");
                        }
                        lookFor(LK_VALUE | LK_NUMBER);
                        break;

                    case Value:
                        check(LK_VALUE);
                        parseValue(tree, attrName, mCurToken.stringValue());
                        lookFor(LK_ID | LK_EndArrowBracket | LK_SLASH);
                        break;

                    case Int:
                        check(LK_INT);
                        tree.addInlineStyle(attrName, mCurToken.intValue());
                        lookFor(LK_ID | LK_EndArrowBracket);
                        break;

                    case Double:
                        check(LK_DOUBLE);
                        tree.addInlineStyle(attrName, mCurToken.doubleValue());
                        lookFor(LK_ID | LK_EndArrowBracket);
                        break;

                    case Inner:
                        check(LK_INNER);
                        if (isSwallowInnerTag(tree.getType())) {
                            tree.appendText(mCurToken.stringValue());
                        } else {
                            HNDomTree innerChild = new HNDomTree(tree, HtmlTag.INNER_TREE_TAG,
                                    innerCount++);
                            tree.addChild(innerChild);
                            innerChild.appendText(mCurToken.stringValue());
                        }

                        lookFor(LK_StartArrowBracket);
                        break;
                    // for <a/> case
                    case Slash:

                        check(LK_SLASH);

                        lookFor(LK_EndArrowBracket);

                        scan();

                        if (mCurToken.type() != EndAngleBracket) {
                            mSyntaxErrorHandler.throwException("unknown state, slash should be "
                                    + "followed by " + ">, " + "but currently " + mCurToken.type());
                        }

                        bracketPair--;
                        if (bracketPair != 0) {
                            mSyntaxErrorHandler.throwException("< > must be in pairs, " + ", " +
                                    "current " + "bracket" + " pair is " + bracketPair);
                        }
                        callback.onLeaveParse();
                        return;

                    default:
                        mSyntaxErrorHandler.throwException("unknown token " + mCurToken.toString());


                }

            }
        } catch (EOFException e) {
            if (meetEndTag) {
                mSyntaxErrorHandler.throwException("View Tag should ends with </");
            }
        }
    }


    private StringBuilder mStyleKeyCache = new StringBuilder();

    private void parseStyle(@NonNull HNDomTree tree, @NonNull String styleString) {
        CssParser.parseInlineStyle(styleString, mStyleKeyCache, mStyleCache);

        for (Map.Entry<String, Object> entry : mStyleCache.entrySet()) {
            tree.addInlineStyle(entry.getKey(), entry.getValue());
        }
    }

    private boolean isLookingFor(int status) {
        return (mLookFor & status) != 0;
    }

    private void lookFor(int status) {
        mLookFor = 0;
        mLookFor |= status;
    }

    private void scan() throws EOFException, HNSyntaxError {
        if (mReserved) {
            HNLog.d(HNLog.PARSER, "Reprocess token ->" + mCurToken);
            mReserved = false;
            return;
        }
        if (mCurToken != null) {
            mCurToken.recycle();
        }
        mCurToken = mLexer.scan();
        if (mCurToken != null) {
            mark(mCurToken.getLine(), mCurToken.getColumn());
        } else {
            mark(-1, -1);
        }
        HNLog.d(HNLog.PARSER, "Process token ->" + mCurToken);
    }

    private void scan(boolean reserved) throws EOFException, HNSyntaxError {
        scan();
        mReserved = reserved;
    }

    private void scanFor(@NonNull TokenType tokenType) throws EOFException, HNSyntaxError {
        scan();

        if (mCurToken.type() != tokenType) {
            mSyntaxErrorHandler.throwException("syntax error, should be " + tokenType.toString()
                    + "， but " + "current is " + mCurToken.toString());
        }
    }

    private void scanFor(@NonNull TokenType... tokenTypes) throws EOFException, HNSyntaxError {
        for (TokenType tokenType : tokenTypes) {
            scanFor(tokenType);
        }
    }

    private void check(int status) throws HNSyntaxError, EOFException {
        if (!isLookingFor(status)) {
            mSyntaxErrorHandler.throwException(" Looking for " + lookForToString(status) + ", " +
                    "but" + " " + "currently is " + lookForToString(mLookFor));
        }
    }

    private void mark(long line, long column) {
        mMarkedColumn = column;
        mMarkedLine = line;
    }

    private void parseValue(HNDomTree tree, String parameterName, String valueStr) {
        switch (parameterName) {
            case Styles.ATTR_STYLE:
                parseStyle(tree, valueStr);
                break;
            case ID:
                tree.setId(valueStr);
                break;
            case CLAZZ:
                tree.setClazz(ParametersUtils.splitByEmpty(valueStr));
                break;
            default:
                tree.addInlineStyle(parameterName, valueStr);
                break;
        }
    }

    public Map<String, Object> getStyleCache() {
        return mStyleCache;
    }

    private static String lookForToString(int lookFor) {
        StringBuilder sb = new StringBuilder("[ ");

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

    @Override
    public long getLine() {
        return mMarkedLine;
    }

    @Override
    public long getColumn() {
        return mMarkedColumn;
    }

    @Override
    public void onSyntaxException() throws EOFException, HNSyntaxError {
        scan();
    }

}
