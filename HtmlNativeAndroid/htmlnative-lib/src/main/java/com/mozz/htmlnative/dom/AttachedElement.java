package com.mozz.htmlnative.dom;

import android.text.TextUtils;

import java.util.List;

/**
 * @author Yang Tao, 17/5/12.
 */

public class AttachedElement implements DomElement {

    private String mType;
    private String[] mClazz;
    private String mId;

    @Override
    public String getType() {
        return mType;
    }

    @Override
    public String[] getClazz() {
        return mClazz;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public DomElement getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List children() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasClazz() {
        return mClazz != null && mClazz.length > 0;
    }

    @Override
    public boolean hasId() {
        return !TextUtils.isEmpty(mId);
    }

    @Override
    public CharSequence getInner() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setType(String type) {
        mType = type;
    }

    @Override
    public void setClazz(String[] clazz) {
        mClazz = clazz;
    }

    @Override
    public void setId(String id) {
        mId = id;
    }

    /**
     * Only clone when domElement is not {@link AttachedElement}
     *
     * @param domElement {@link DomElement}
     * @return cloned one, if domElement is not an AttachedElement; otherwise return just
     * domElement itself
     */
    public static AttachedElement cloneIfNecessary(DomElement domElement) {
        if (domElement instanceof AttachedElement) {
            return (AttachedElement) domElement;
        } else {
            return cloneFrom(domElement);
        }
    }

    public static AttachedElement cloneFrom(DomElement domElement) {
        AttachedElement attachedElement = new AttachedElement();
        attachedElement.setId(domElement.getId());
        attachedElement.setClazz(domElement.getClazz());
        attachedElement.setType(domElement.getType());
        return attachedElement;
    }
}
