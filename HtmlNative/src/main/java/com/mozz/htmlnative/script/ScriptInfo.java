package com.mozz.htmlnative.script;

import com.mozz.htmlnative.token.Token;

/**
 * @author Yang Tao, 17/3/20.
 */

public class ScriptInfo {

    public static final int SCRIPT_JAVASCRIPT = 0x01;
    public static final int SCRIPT_LUA = 0x02;

    private final Token token;
    private final int type;

    public ScriptInfo(Token scriptToken, String type) {
        token = scriptToken;
        this.type = parseType(type);
    }

    private static int parseType(String typeString) {
        if (typeString == null) {
            return SCRIPT_JAVASCRIPT;
        }

        switch (typeString) {
            case "text/javascript":
                return SCRIPT_JAVASCRIPT;
            case "text/lua":
                return SCRIPT_LUA;
            default:
                return SCRIPT_JAVASCRIPT;
        }
    }

    @Override
    public String toString() {
        return "[" + (type == SCRIPT_JAVASCRIPT ? "javascript" : "lua") + ":" + token.stringValue
                () + "]";
    }
}
