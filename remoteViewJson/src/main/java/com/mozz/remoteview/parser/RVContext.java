package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.code.Code;

/**
 * Created by Yang Tao on 17/2/23.
 */

public final class RVContext {
    RVDomTree mRootTree;
    FunctionTable mFunctionTable;

    RVContext() {
        mFunctionTable = new FunctionTable();
    }

    void putFunction(String functionName, String code) {
        mFunctionTable.putFunction(functionName, code);
    }

    /**
     *
     * @param functionName
     * @return
     */
    public Code retrieveCode(String functionName) {
        return mFunctionTable.retrieveCode(functionName);
    }
}
