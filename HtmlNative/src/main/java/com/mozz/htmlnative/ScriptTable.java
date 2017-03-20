package com.mozz.htmlnative;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;


final class ScriptTable {

    private static final String[] RESERVED_FUNCTION = {"create", "created"};
    static final int CREATE = 0;
    static final int CREATED = 1;

    private Map<String, Script> mFunctions;
    @NonNull
    private Script[] mReserved = new Script[RESERVED_FUNCTION.length];

    ScriptTable() {
        mFunctions = new HashMap<>();
    }

    void putFunction(@NonNull String functionName, String code) {
        if (!putReserved(functionName, code)) {
            mFunctions.put(functionName, Script.toCode(functionName, code));
        }
    }

    Script retrieveCode(String functionName) {
        return mFunctions.get(functionName);
    }

    @Override
    public String toString() {
        return mFunctions.toString();
    }

    private boolean putReserved(@NonNull String functionName, String code) {
        if (functionName.equals(RESERVED_FUNCTION[CREATED])) {
            mReserved[CREATED] = Script.toCode(functionName, code);
            return true;
        } else if (functionName.equals(RESERVED_FUNCTION[CREATE])) {
            mReserved[CREATE] = Script.toCode(functionName, code);
            return true;
        }

        return false;
    }

    Script retrieveReserved(int reservedId) {
        return mReserved[reservedId];
    }
}
