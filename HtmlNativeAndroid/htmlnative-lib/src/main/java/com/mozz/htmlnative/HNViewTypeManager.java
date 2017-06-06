package com.mozz.htmlnative;

import android.support.annotation.MainThread;

import com.mozz.htmlnative.css.InheritStylesRegistry;
import com.mozz.htmlnative.css.stylehandler.StyleHandlerFactory;

import java.util.Set;

/**
 * @author Yang Tao, 17/6/6.
 */

final class HNViewTypeManager {

    private HNViewTypeManager() {
    }

    @MainThread
    static synchronized void registerViewType(HNViewType viewType) {
        if (viewType.getViewClass() != null && viewType.getHTMLType() != null) {
            ViewTypeRelations.registerExtraView(viewType.getViewClass().getName(), viewType
                    .getHTMLType());

            StyleHandlerFactory.registerExtraStyleHandler(viewType.getViewClass(), viewType);
            HNRenderer.registerViewFactory(viewType.getViewClass().getName(), viewType);

            Set<String> inheritStyleNames = viewType.onInheritStyleNames();
            if (inheritStyleNames != null && !inheritStyleNames.isEmpty()) {
                for (String style : inheritStyleNames) {
                    if (!InheritStylesRegistry.isPreserved(style)) {
                        InheritStylesRegistry.register(style);
                    }
                }
            }
        }
    }

    static synchronized void unregisterViewType(HNViewType viewType) {
        ViewTypeRelations.unregisterExtraView(viewType.getHTMLType());
        StyleHandlerFactory.unregisterExtraStyleHandler(viewType.getViewClass());
        HNRenderer.unregisterViewFactory(viewType.getViewClass().getName());
    }

    static synchronized void clearAllViewType() {
        ViewTypeRelations.clearAllExtraView();
        StyleHandlerFactory.clearExtraStyleHandler();
        HNRenderer.clearAllViewFactory();
    }
}
