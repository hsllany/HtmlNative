package com.mozz.htmlnativedemo;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;

/**
 * @author Yang Tao, 17/4/14.
 */

public class AssetsUtils {
    private AssetsUtils() {

    }

    public static String[] allFiles(Context context) throws IOException {
        AssetManager manager = context.getAssets();
        return manager.list("");
    }
}
