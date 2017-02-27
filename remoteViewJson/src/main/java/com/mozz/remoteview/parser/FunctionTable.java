package com.mozz.remoteview.parser;

import android.util.ArrayMap;

import com.mozz.remoteview.parser.code.Code;

import java.util.HashMap;
import java.util.Map;


final class FunctionTable {

    private Map<String, Code> mFunctions;

    FunctionTable() {
        mFunctions = new HashMap<>();
    }

    void putFunction(String functionName, String code) {
        mFunctions.put(functionName, Code.toCode(functionName, code));
    }

    Code retrieveCode(String functionName) {
        return mFunctions.get(functionName);
    }

    @Override
    public String toString() {
        return mFunctions.toString();
    }
}
