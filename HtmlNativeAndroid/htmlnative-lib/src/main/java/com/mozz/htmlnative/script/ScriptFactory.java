package com.mozz.htmlnative.script;

import android.util.ArrayMap;
import android.util.Log;

import com.mozz.htmlnative.HNSandBoxContext;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/21.
 */

public final class ScriptFactory {

    private ScriptFactory() {
    }

    private static Map<String, Class<? extends ScriptRunner>> sSupportedScriptType = new
            ArrayMap<>();

    public static ScriptRunner createRunner(String type, HNSandBoxContext context) {
        Class<? extends ScriptRunner> clazz = sSupportedScriptType.get(type);
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static boolean register(Class<? extends ScriptRunner> runnerClazz) {
        Lauguage lauguage = runnerClazz.getAnnotation(Lauguage.class);
        if (lauguage != null && lauguage.type() != null) {
            String type = lauguage.type();
            if (sSupportedScriptType.containsKey(type)) {
                return false;
            }

            sSupportedScriptType.put(type, runnerClazz);
            return true;
        } else {
            Log.e("HtmlNative", "Runner must have Lauguage Annotation!");
            return false;
        }
    }
}
