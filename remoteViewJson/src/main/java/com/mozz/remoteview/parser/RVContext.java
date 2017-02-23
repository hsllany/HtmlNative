package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.code.Code;

/**
 * Created by Yang Tao on 17/2/23.
 */

public final class RVContext {
    RVDomTree mRootTree;
    FunctionTable mFunctionTable;

    public RVContext() {
        mFunctionTable = new FunctionTable();
    }

    public void putFunction(String functionName, String code) {
        mFunctionTable.putFunction(functionName, code);
    }

    public Code retriveCode(String functionName) {
        return mFunctionTable.retrieveCode(functionName);
    }
}
