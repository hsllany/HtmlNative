package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.mozz.remoteview.reader.FileTextReader;

import java.io.InputStream;
import java.util.Map;

final class RVSegment {
    RVDomTree mRootTree;
    private ScriptTable mScriptTable;
    AttrsSet mAttrs;

    boolean mHasScriptEmbed = false;

    @NonNull
    private static Map<String, RVSegment> sCache = new ArrayMap<>();

    private static final Object sCacheLock = new Object();

    RVSegment() {
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
    public static RVSegment load(@NonNull InputStream stream) throws RVSyntaxError {
        Parser parser = new Parser(new FileTextReader(stream));
        return parser.process();
    }

    //TODO finish the cache of RVSegment
    public static RVSegment load(@NonNull InputStream stream, String tag) throws RVSyntaxError {
        synchronized (sCacheLock) {
            RVSegment module = sCache.get(tag);
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
}
