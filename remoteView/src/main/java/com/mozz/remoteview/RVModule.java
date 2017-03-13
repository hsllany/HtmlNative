package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.mozz.remoteview.script.Code;
import com.mozz.remoteview.reader.FileCodeReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

final class RVModule {
    RVDomTree mRootTree;
    private FunctionTable mFunctionTable;
    AttrsSet mAttrs;

    boolean mHasScriptEmbed = false;

    @NonNull
    private static Map<String, RVModule> sCache = new ArrayMap<>();

    private static final Object sCacheLock = new Object();

    RVModule() {
        mAttrs = new AttrsSet(this);
        mFunctionTable = new FunctionTable();
    }

    void putFunction(String functionName, String code) {
        mFunctionTable.putFunction(functionName, code);
        if (!mHasScriptEmbed)
            mHasScriptEmbed = true;
    }

    /**
     * @param functionName
     * @return
     */
    public Code retrieveCode(String functionName) {
        return mFunctionTable.retrieveCode(functionName);
    }

    public Code retrieveReserved(int reservedCode) {
        return mFunctionTable.retrieveReserved(reservedCode);
    }

    @NonNull
    public static RVModule load(@NonNull InputStream stream) throws RVSyntaxError {
        Parser parser = new Parser(new FileCodeReader(stream));
        return parser.process();
    }

    //TODO finish the cache of RVModule
    public static RVModule load(@NonNull InputStream stream, String tag) throws RVSyntaxError {
        synchronized (sCacheLock) {
            RVModule module = sCache.get(tag);
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
