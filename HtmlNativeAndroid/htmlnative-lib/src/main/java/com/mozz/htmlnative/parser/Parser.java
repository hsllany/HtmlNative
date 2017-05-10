package com.mozz.htmlnative.parser;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSegment;
import com.mozz.htmlnative.HtmlTag;
import com.mozz.htmlnative.Tracker;
import com.mozz.htmlnative.css.Styles;
import com.mozz.htmlnative.dom.HNDomTree;
import com.mozz.htmlnative.dom.Meta;
import com.mozz.htmlnative.exception.HNSyntaxError;
import com.mozz.htmlnative.reader.TextReader;
import com.mozz.htmlnative.script.ScriptInfo;
import com.mozz.htmlnative.token.Token;
import com.mozz.htmlnative.token.TokenType;
import com.mozz.htmlnative.utils.ParametersUtils;

import java.io.EOFException;
import java.util.HashMap;
import java.util.Map;

import static com.mozz.htmlnative.HNEnvironment.PERFORMANCE_TAG;
import static com.mozz.htmlnative.HtmlTag.isSwallowInnerTag;
import static com.mozz.htmlnative.parser.StyleItemParser.parseStyleSingle;
import static com.mozz.htmlnative.token.TokenType.EndAngleBracket;
import static com.mozz.htmlnative.token.TokenType.Equal;
import static com.mozz.htmlnative.token.TokenType.Exclamation;
import static com.mozz.htmlnative.token.TokenType.Head;
import static com.mozz.htmlnative.token.TokenType.Html;
import static com.mozz.htmlnative.token.TokenType.Id;
import static com.mozz.htmlnative.token.TokenType.Inner;
import static com.mozz.htmlnative.token.TokenType.Meta;
import static com.mozz.htmlnative.token.TokenType.Script;
import static com.mozz.htmlnative.token.TokenType.Slash;
import static com.mozz.htmlnative.token.TokenType.StartAngleBracket;
import static com.mozz.htmlnative.token.TokenType.Style;
import static com.mozz.htmlnative.token.TokenType.Template;
import static com.mozz.htmlnative.token.TokenType.Title;

/**
 * @author YangTao7
 */
public final class Parser {

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

    @Nullable
    private Token mCurToken;

    private boolean mReserved = false;

    private Map<String, Object> styleCache = new HashMap<>();

    private Tracker mTracker;

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
        mCssParser = new CssParser(mLexer, this);

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
            return segment;
        }
    }

    @NonNull
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
                Log.e(TAG, "must init with <template> or <script>");
                throw new HNSyntaxError("must init with <template> or <script>", mLexer.line(),
                        mLexer.column());

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
            Log.e(TAG, "Look for script, but " + mCurToken.toString());
            throw new HNSyntaxError("Look for script, but " + mCurToken.toString(), mLexer.line()
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
                    if (scriptToken.type() != TokenType.ScriptCode) {
                        throw new HNSyntaxError("Expect code, but meet " + scriptToken.type()
                                .toString(), mLexer.line(), mLexer.column());
                    }

                    segment.setScriptInfo(new ScriptInfo(scriptToken, type));
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

    private void processHead(HNSegment segment) throws HNSyntaxError, EOFException {
        if (mCurToken.type() != TokenType.Head) {
            Log.e(TAG, "Look for \"head\", but " + mCurToken.toString());
            throw new HNSyntaxError("Look for \"head\", but " + mCurToken.toString(), mLexer.line
                    (), mLexer.column());
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
                    throw new HNSyntaxError("unknown " + mCurToken.toString() + " token in " +
                            "<style>", mLexer.column(), mLexer.line());
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
            Log.e(TAG, "Look for head, but " + mCurToken.toString());
            throw new HNSyntaxError("Look for head, but " + mCurToken.toString(), mLexer.line(),
                    mLexer.column());
        }

        scanFor(EndAngleBracket);
        scanFor(Inner);

        String title = mCurToken.stringValue();
        segment.getHead().setTitle(title);

        scanFor(StartAngleBracket, Slash, Title, EndAngleBracket);
    }

    private void processMeta(HNSegment segment) throws HNSyntaxError, EOFException {
        if (mCurToken.type() != Meta) {
            Log.e(TAG, "Look for meta, but " + mCurToken.toString());
            throw new HNSyntaxError("Look for meta, but " + mCurToken.toString(), mLexer.line(),
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
                    Log.e(TAG, "Unknown token " + mCurToken.toString() + " when " +
                            "parsing <meta>" + mCurToken.toString());
                    throw new HNSyntaxError("Unknown token " + mCurToken.toString() + " when " +
                            "parsing <meta>" + mCurToken.toString(), mLexer.line(), mLexer.column
                            ());
            }
        }


    }

    private void processTemplate(HNDomTree tree) throws HNSyntaxError {

        long timeStart = SystemClock.currentThreadTimeMillis();

        if (mCurToken.type() != Template) {
            Log.e(TAG, "Look for Template, but " + mCurToken.toString());
            throw new HNSyntaxError("Look for Template, but " + mCurToken.toString(), mLexer.line
                    (), mLexer.column());
        }

        tree.setType(mCurToken.stringValue());
        processInternal(tree);

        mTracker.record("Parse Html", SystemClock.currentThreadTimeMillis() - timeStart);
    }

    private void processInternal(@NonNull HNDomTree tree) throws HNSyntaxError {
        processInternal(tree, tree);
    }

    /**
     * parse the tree recursively
     *
     * @throws HNSyntaxError
     */
    private void processInternal(@NonNull HNDomTree tree, @NonNull ParseCallback callback) throws
            HNSyntaxError {
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
                                Log.e(TAG, "View tag should be in pairs, current " +
                                        "is<" + tree.getType() + "></" + mCurToken.value() +
                                        ">");
                                throw new HNSyntaxError("View tag should be in pairs, current " +
                                        "is<" + tree.getType() + "></" + mCurToken.value() +
                                        ">", mLexer.line(), mLexer.column());
                            }

                            scan();

                            if (mCurToken.type() != EndAngleBracket) {
                                Log.e(TAG, "View tag must be end with >");
                                throw new HNSyntaxError("View tag must be end with >", mLexer
                                        .line(), mLexer.column());
                            }

                            bracketPair--;
                            if (bracketPair != 0) {
                                Log.e(TAG, "< > must be in pairs, " + ", current bracket" +
                                        " pair is " + bracketPair);
                                throw new HNSyntaxError("< > must be in pairs, " + ", current " +
                                        "bracket" +
                                        " pair is " + bracketPair, mLexer.line(), mLexer.column());
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
                            Log.e(TAG, "attrName is null, please check the state");
                            throw new HNSyntaxError("attrName is null, please check the state",
                                    mLexer.line(), mLexer.column());
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
                            Log.e(TAG, "unknown state, slash should be followed by " +
                                    ">, " +
                                    "but currently " + mCurToken.type());
                            throw new HNSyntaxError("unknown state, slash should be followed by " +
                                    ">, " +
                                    "but currently " + mCurToken.type(), mLexer.line(), mLexer
                                    .column());
                        }

                        bracketPair--;
                        if (bracketPair != 0) {
                            Log.e(TAG, "< > must be in pairs, " + ", current bracket" +
                                    " pair is " + bracketPair);
                            throw new HNSyntaxError("< > must be in pairs, " + ", current bracket" +
                                    " pair is " + bracketPair, mLexer.line(), mLexer.column());
                        }
                        callback.onLeaveParse();
                        return;

                    default:
                        Log.e(TAG, "unknown token " + mCurToken.toString());
                        throw new HNSyntaxError("unknown token " + mCurToken.toString(), mLexer
                                .line(), mLexer.column());


                }

            }
        } catch (EOFException e) {
            if (meetEndTag) {
                Log.e(TAG, "View Tag should ends with </");
                throw new HNSyntaxError("View Tag should ends with </", mLexer.line(), mLexer
                        .column());
            }
        }
    }

    private StringBuilder mStyleKeyCache = new StringBuilder();

    private void parseStyle(@NonNull HNDomTree tree, @NonNull String styleString) {
        mStyleKeyCache.setLength(0);

        String key = null;

        styleCache.clear();

        boolean inBracket = false;
        for (int i = 0; i < styleString.length(); i++) {
            char c = styleString.charAt(i);

            if (c == '(') {
                inBracket = true;
                mStyleKeyCache.append(c);
            } else if (c == ')') {
                inBracket = false;
                mStyleKeyCache.append(c);
            } else if (c == ';') {
                Object value = styleCache.get(StyleItemParser.parseKey(key));
                CssParser.StyleHolder parsedStyle;
                if (value != null) {
                    parsedStyle = parseStyleSingle(key, mStyleKeyCache.toString(), value);
                } else {
                    parsedStyle = parseStyleSingle(key, mStyleKeyCache.toString(), null);
                }
                styleCache.put(parsedStyle.key, parsedStyle.obj);
                mStyleKeyCache.setLength(0);
            } else if (c == ':' && !inBracket) {
                key = mStyleKeyCache.toString();
                mStyleKeyCache.setLength(0);
            } else {
                if (c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\f' || c == '\b') {
                    continue;
                }
                mStyleKeyCache.append(c);
            }
        }

        if (key != null) {
            Object value = styleCache.get(StyleItemParser.parseKey(key));
            CssParser.StyleHolder parsedStyle;
            if (value != null) {
                parsedStyle = parseStyleSingle(key, mStyleKeyCache.toString(), value);
            } else {
                parsedStyle = parseStyleSingle(key, mStyleKeyCache.toString(), null);
            }
            styleCache.put(parsedStyle.key, parsedStyle.obj);
        }

        for (Map.Entry<String, Object> entry : styleCache.entrySet()) {
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

        HNLog.d(HNLog.PARSER, "Process token ->" + mCurToken);

    }

    private void scan(boolean reserved) throws EOFException, HNSyntaxError {
        scan();
        mReserved = reserved;
    }

    private void scanFor(@NonNull TokenType tokenType) throws EOFException, HNSyntaxError {
        scan();

        if (mCurToken.type() != tokenType) {
            Log.e(TAG, "syntax error, should be " + tokenType.toString() +
                    "， but current is " + mCurToken.toString());
            throw new HNSyntaxError("syntax error, should be " + tokenType.toString() +
                    "， but current is " + mCurToken.toString(), mLexer.line(), mLexer.column());
        }
    }

    private void scanFor(@NonNull TokenType... tokenTypes) throws EOFException, HNSyntaxError {
        for (TokenType tokenType : tokenTypes) {
            scanFor(tokenType);
        }
    }

    private void check(int status) throws HNSyntaxError {
        if (!isLookingFor(status)) {
            Log.e(TAG, " Looking for " + lookForToString(status) + ", but " +
                    "currently is " +
                    lookForToString(mLookFor));
            throw new HNSyntaxError(" Looking for " + lookForToString(status) + ", but " +
                    "currently is " +
                    lookForToString(mLookFor), mLexer.line(), mLexer.column());
        }
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
        return styleCache;
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

}
