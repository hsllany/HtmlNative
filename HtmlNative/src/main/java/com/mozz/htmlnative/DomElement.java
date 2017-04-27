package com.mozz.htmlnative;

import java.util.List;

/**
 * @author Yang Tao, 17/4/25.
 */
public interface DomElement {
    String getType();
    String getClazz();
    String getId();
    DomElement getParent();
    List children();
    boolean hasClazz();
    boolean hasId();


}
