package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.mozz.htmlnative.reader.FileTextReader;
import com.mozz.htmlnative.script.ScriptInfo;

import java.io.InputStream;
import java.util.Map;

final class HNSegment {

    HNDomTree mRootTree;
    AttrsSet mAttrs;
    boolean mHasScriptEmbed;
    ScriptInfo mScriptInfo;
    HNHead mHead;
    StyleSheet mStyleSheet;

    @NonNull
    private static Map<String, HNSegment> sCache = new ArrayMap<>();
    private static final Object sCacheLock = new Object();

    HNSegment() {
        mAttrs = new AttrsSet("inLineStyle");
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
}
