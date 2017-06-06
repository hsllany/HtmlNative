package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.mozz.htmlnative.css.stylehandler.StyleHandler;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.LayoutParamsCreator;

import java.util.Set;

/**
 * @author Yang Tao, 17/3/3.
 */

public abstract class HNViewType<T extends View> extends StyleHandler implements HNRenderer
        .ViewFactory<T> {

    @NonNull
    public abstract Class<T> getViewClass();

    @NonNull
    public abstract String getHTMLType();

    @Override
    public final void apply(Context context, View v, DomElement domElement, View parent,
                            LayoutParamsCreator paramsCreator, String params, Object
                                        value) throws AttrApplyException {
        this.onSetStyle(context, v, parent, paramsCreator, params, value);
    }

    @Override
    public final void setDefault(Context context, View v, DomElement domElement,
                                 LayoutParamsCreator paramsCreator, View parent) throws
            AttrApplyException {
        this.onSetDefaultStyle(context, v, paramsCreator, parent);
    }

    public abstract void onSetStyle(Context context, View v, View parent, LayoutParamsCreator
            layoutCreator, String styleName, Object style);

    public abstract void onSetDefaultStyle(Context context, View v, LayoutParamsCreator layoutParamsCreator, View parent);

    public Set<String> onInheritStyleNames() {
        return null;
    }
}
