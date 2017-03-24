package com.mozz.htmlnative.attrs;

/**
 * @author Yang Tao, 17/3/24.
 */

public class BackgroundStyle {
    private String url = "";
    private int color;
    private boolean colorSet = false;
    public int renderMode;

    @Override
    public String toString() {
        return "[background:url=" + url + ", color=" + color + ", renderMode=" + renderMode + "]";
    }

    public void setColor(int color) {
        this.color = color;
        this.colorSet = true;
    }

    public boolean isColorSet() {
        return this.colorSet;
    }

    public int getColor() {
        return this.color;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
