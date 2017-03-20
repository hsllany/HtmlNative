package com.mozz.htmlnative;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.mozz.htmlnative.reader.FileTextReader;
import com.mozz.htmlnative.script.ScriptInfo;

import java.io.InputStream;
import java.util.Map;

final class RVSegment {
    RVDomTree mRootTree;

    private ScriptTable mScriptTable;

    AttrsSet mAttrs;

    boolean mHasScriptEmbed = false;

    ScriptInfo mScriptInfo;

    @NonNull
    private static Map<String, RVSegment> sCache = new ArrayMap<>();

    private static final Object sCacheLock = new Object();

    private MetaData mMeta = null;

    private String mTitle = null;

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

    public boolean containsMeta(Meta key) {
        lazyInitialMetaData();
        return mMeta.contains(key);
    }

    public Meta getMeta(String metaName) {
        lazyInitialMetaData();
        return mMeta.get(metaName);
    }

    public void clearMeta() {
        if (mMeta != null) {
            mMeta.clear();
        }
    }

    public Meta putMeta(Meta value) {
        lazyInitialMetaData();
        return mMeta.put(value);
    }

    public Meta removeMeta(Meta key) {
        lazyInitialMetaData();
        return mMeta.remove(key);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    private void lazyInitialMetaData() {
        if (mMeta == null) {
            mMeta = new MetaData();
        }
    }

    @Override
    public String toString() {
        String metaStr = "";
        if (mMeta != null) {
            metaStr = mMeta.toString();
        }
        return "[RVSegment: title=" + mTitle + ", meta=" + metaStr + "]";
    }
}
