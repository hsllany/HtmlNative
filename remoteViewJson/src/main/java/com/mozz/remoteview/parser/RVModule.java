package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.code.Code;

public final class RVModule {
    RVDomTree mRootTree;
    FunctionTable mFunctionTable;
    AttrsSet mAttrs;

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
}
