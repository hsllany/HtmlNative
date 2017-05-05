package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.mozz.htmlnative.css.AttrsSet;
import com.mozz.htmlnative.css.StyleSheet;
import com.mozz.htmlnative.dom.HNDomTree;
import com.mozz.htmlnative.dom.HNHead;
import com.mozz.htmlnative.exception.HNSyntaxError;
import com.mozz.htmlnative.reader.FileTextReader;
import com.mozz.htmlnative.script.ScriptInfo;

import java.io.InputStream;
import java.util.Map;

public final class HNSegment {

    HNDomTree mDom;
    boolean mHasScriptEmbed;
    ScriptInfo mScriptInfo;
    HNHead mHead;

    AttrsSet mInlineStyles;
    StyleSheet mStyleSheet;

    @NonNull
    private static Map<String, HNSegment> sCache = new ArrayMap<>();
    private static final Object sCacheLock = new Object();

    HNSegment() {
        mInlineStyles = new AttrsSet("Inline-Style");
        mHead = new HNHead();
        mHasScriptEmbed = false;
        mStyleSheet = new StyleSheet();
    }

    @NonNull
    public static HNSegment load(@NonNull InputStream stream) throws HNSyntaxError {
        Parser parser = new Parser(new FileTextReader(stream));
        return parser.process();
    }

    //TODO finish the cache of HNSegment
    public static HNSegment load(@NonNull InputStream stream, String type) throws HNSyntaxError {
        synchronized (sCacheLock) {
            HNSegment module = sCache.get(type);
            if (module != null) {
                return module;
            } else {
                module = load(stream);
                sCache.put(type, module);
                return module;
            }
        }
    }

    public static void clearCache() {
        synchronized (sCacheLock) {
            sCache.clear();
        }
    }


    @Override
    public String toString() {
        //TODO
        return mHead.toString();
    }

    public void newAttr(@NonNull AttrsSet.AttrsOwner tree) {
        mInlineStyles.register(tree);
    }

    public void put(@NonNull AttrsSet.AttrsOwner tree, String paramsKey, @NonNull Object value) {
        mInlineStyles.put(tree, paramsKey, value);
    }

    public String attrToString(AttrsSet.AttrsOwner owner) {
        return mInlineStyles.toString(owner);
    }
}
