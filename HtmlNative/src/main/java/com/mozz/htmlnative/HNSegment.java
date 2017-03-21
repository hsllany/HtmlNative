package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.mozz.htmlnative.reader.FileTextReader;
import com.mozz.htmlnative.script.ScriptInfo;

import java.io.InputStream;
import java.util.Map;

final class HNSegment {
    HNDomTree mRootTree;

    private ScriptTable mScriptTable;

    AttrsSet mAttrs;

    boolean mHasScriptEmbed = false;

    ScriptInfo mScriptInfo;

    HNHead mHead = new HNHead();

    @NonNull
    private static Map<String, HNSegment> sCache = new ArrayMap<>();

    private static final Object sCacheLock = new Object();

    HNSegment() {
        mAttrs = new AttrsSet(this);
        mScriptTable = new ScriptTable();
    }

    void putFunction(String functionName, String code) {
        mScriptTable.putFunction(functionName, code);
        if (!mHasScriptEmbed) {
            mHasScriptEmbed = true;
        }
    }

    /**
     * @param functionName
     * @return
     */
    public Script retrieveCode(String functionName) {
        return mScriptTable.retrieveCode(functionName);
    }

    public Script retrieveReserved(int reservedCode) {
        return mScriptTable.retrieveReserved(reservedCode);
    }

    @NonNull
    public static HNSegment load(@NonNull InputStream stream) throws HNSyntaxError {
        Parser parser = new Parser(new FileTextReader(stream));
        return parser.process();
    }

    //TODO finish the cache of HNSegment
    public static HNSegment load(@NonNull InputStream stream, String tag) throws HNSyntaxError {
        synchronized (sCacheLock) {
            HNSegment module = sCache.get(tag);
            if (module != null) {
                return module;
            } else {
                module = load(stream);
                sCache.put(tag, module);
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
