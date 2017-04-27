package com.mozz.htmlnative.css;

import com.mozz.htmlnative.DomElement;
import com.mozz.htmlnative.HNLog;

import java.util.Arrays;

/**
 * @author Yang Tao, 17/4/25.
 */
public final class CssStack {
    private int level;
    private int index;
    private int[] cssCount;
    private int[] css;

    private static final int MAX_DEPTH = 20;

    public CssStack() {
        cssCount = new int[MAX_DEPTH];
        css = new int[50];
        reset();
    }

    public void push() {
        level++;
    }

    public void updateCss(int cssId) {
        css[index++] = cssId;
        cssCount[level]++;
    }

    public void pop() {
        this.index -= cssCount[level];
        cssCount[level] = 0;
        level--;
    }

    public void reset() {
        level = -1;
        index = 0;
        Arrays.fill(css, -1);
    }

    public int css(int position) {
        return css[position];
    }

    public int size() {
        return index;
    }

    private void debugCssParent(String msg) {
        HNLog.d(HNLog.RENDER, "---------" + msg + "---------");
        HNLog.d(HNLog.RENDER, "parentIndex=" + index);
        HNLog.d(HNLog.RENDER, "level=" + level);
        HNLog.d(HNLog.RENDER, "stackCss=" + Arrays.toString(css));
        HNLog.d(HNLog.RENDER, "stackSize=" + Arrays.toString(cssCount));
        HNLog.d(HNLog.RENDER, "------------------");
    }

}
