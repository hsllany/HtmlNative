package com.mozz.htmlnative.script;

import com.mozz.htmlnative.parser.token.Token;

/**
 * @author Yang Tao, 17/3/20.
 */

public class ScriptInfo {

    private final Token token;
    private final String type;

    public ScriptInfo(Token scriptToken, String type) {
        token = scriptToken;
        this.type = type;
    }

    public String type() {
        return type;
    }

    public String code() {
        return token.stringValue();
    }

    public static ScriptInfo newScript(Token scriptToken, String scriptTypeName) {
        return new ScriptInfo(scriptToken, scriptTypeName);
    }

    @Override
    public String toString() {
        return type;
    }
}
