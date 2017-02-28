package com.mozz.remoteview;

import android.util.ArrayMap;

import com.mozz.remoteview.code.Code;
import com.mozz.remoteview.reader.FileCodeReader;

import java.io.InputStream;
import java.util.Map;

public final class RVModule {
    RVDomTree mRootTree;
    FunctionTable mFunctionTable;
    AttrsSet mAttrs;

    private static Map<String, RVModule> sCache = new ArrayMap<>();

    private static Object sCacheLock = new Object();

    RVModule() {
        mFunctionTable = new FunctionTable();
        mAttrs = new AttrsSet(this);
    }

    void putFunction(String functionName, String code) {
        mFunctionTable.putFunction(functionName, code);
    }

    /**
     * @param functionName
     * @return
     */
    public Code retrieveCode(String functionName) {
        return mFunctionTable.retrieveCode(functionName);
    }

    public static RVModule load(InputStream stream) throws RVSyntaxError {
        Parser parser = new Parser(new FileCodeReader(stream));
        return parser.process();
    }

    public static RVModule load(InputStream stream, String tag) throws RVSyntaxError {
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
