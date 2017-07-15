package com.mozz.htmlnative.script;

import com.mozz.htmlnative.parser.token.Token;

/**
 * @author Yang Tao, 17/3/20.
 */

public class ScriptInfo {

    private final Token token;
    private final int type;

    public ScriptInfo(Token scriptToken, int type) {
        token = scriptToken;
        this.type = type;
    }

    public int type() {
        return type;
    }

    public String code() {
        return token.stringValue();
    }

    public static ScriptInfo newScript(Token scriptToken, String scriptTypeName) {
        return new ScriptInfo(scriptToken, ScriptFactory.typeOf(scriptTypeName));
    }

    @Override
    public String toString() {
        return "[" + (type == ScriptFactory.JAVASCRIPT ? "javascript" : "lua") + ":" +
                token.stringValue() + "]";
    }
}
