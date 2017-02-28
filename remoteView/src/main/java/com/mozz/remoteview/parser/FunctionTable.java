package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.code.Code;

import java.util.HashMap;
import java.util.Map;


final class FunctionTable {

    private static final String[] RESERVED_FUNCTION = {"create", "created"};
    public static final int CREATE = 0;
    public static final int CREATED = 1;

    private Map<String, Code> mFunctions;
    private Code[] mReserved = new Code[RESERVED_FUNCTION.length];

    FunctionTable() {
        mFunctions = new HashMap<>();
    }

    void putFunction(String functionName, String code) {
        if (!processReserved(functionName, code)) {
            mFunctions.put(functionName, Code.toCode(functionName, code));
        }
    }

    Code retrieveCode(String functionName) {
        return mFunctions.get(functionName);
    }

    @Override
    public String toString() {
        return mFunctions.toString();
    }

    private boolean processReserved(String functionName, String code) {
        if (functionName.equals(RESERVED_FUNCTION[CREATED])) {
            mReserved[CREATED] = Code.toCode(functionName, code);
            return true;
        } else if (functionName.equals(RESERVED_FUNCTION[CREATE])) {
            mReserved[CREATE] = Code.toCode(functionName, code);
            return true;
        }

        return false;
    }

    Code retrieveReserved(int reservedId) {
        return mReserved[reservedId];
    }
}
