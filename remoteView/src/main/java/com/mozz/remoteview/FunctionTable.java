package com.mozz.remoteview;

import android.support.annotation.NonNull;

import com.mozz.remoteview.script.Code;

import java.util.HashMap;
import java.util.Map;


final class FunctionTable {

    private static final String[] RESERVED_FUNCTION = {"create", "created"};
    static final int CREATE = 0;
    static final int CREATED = 1;

    private Map<String, Code> mFunctions;
    @NonNull
    private Code[] mReserved = new Code[RESERVED_FUNCTION.length];

    FunctionTable() {
        mFunctions = new HashMap<>();
    }

    void putFunction(@NonNull String functionName, String code) {
        if (!putReserved(functionName, code)) {
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

    private boolean putReserved(@NonNull String functionName, String code) {
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
