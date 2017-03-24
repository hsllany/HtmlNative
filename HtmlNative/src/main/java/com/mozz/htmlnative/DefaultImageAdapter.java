package com.mozz.htmlnative;

import com.mozz.htmlnative.view.ViewImageAdapter;

/**
 * @author Yang Tao, 17/3/10.
 */

final class DefaultImageAdapter implements ImageViewAdapter {

    static DefaultImageAdapter sInstance;

    static {
        sInstance = new DefaultImageAdapter();
    }

    private DefaultImageAdapter() {
    }

    @Override
    public void setImage(String src, ViewImageAdapter imageView) {
        //do nothing
    }
}
