package com.mozz.htmlnative.dom;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.css.AttrsSet;
import com.mozz.htmlnative.parser.ParseCallback;
import com.mozz.htmlnative.utils.ParametersUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public final class HNDomTree implements ParseCallback, AttrsSet.AttrsOwner, DomElement {

    private static final String TREE_ORDER_PARAMETER = "order";

    private int mDepth;

    private HNDomTree mParent;

    /**
     * Represent the appearance position in HNativeEngine file.
     * Notice this is not the actual position in tree's children. See {@link HNDomTree#mOrder}
     */
    private int mIndex;

    private LinkedList<HNDomTree> mChildren;

    @Nullable
    private String mType;

    private AttrsSet mInlineStyle;

    @Nullable
    private String mInnerText = null;

    /**
     * Id in html
     */
    private String mId = null;

    /**
     * class property in html
     */
    private String[] mClass = null;

    private int mAttrIndex;

    private int mOrder = -1;

    /**
     * Mark whether a tree's children are in order. Set default true, being set to false only when
     * a tree with mOrder!=-1 has been added as child. {@link HNDomTree#addChild(HNDomTree)}
     */
    private boolean mIsInOrder = true;

    public HNDomTree(@NonNull AttrsSet inlineStyle, HNDomTree parent, int depth, int index) {
        this(inlineStyle, null, parent, depth, index);
    }

    private HNDomTree(@NonNull AttrsSet inlineStyle, String tag, HNDomTree parent, int depth, int
            index) {
        mInlineStyle = inlineStyle;
        mType = tag;
        mDepth = depth;
        mParent = parent;
        mIndex = index;
        mChildren = new LinkedList<>();

        inlineStyle.register(this);
    }

    public HNDomTree(@NonNull HNDomTree parent, String nodeName, int index) {
        this(parent.mInlineStyle, nodeName, parent, parent.mDepth + 1, index);
    }

    public void addInlineStyle(String styleName, @NonNull Object style) {
        if (TREE_ORDER_PARAMETER.equalsIgnoreCase(styleName)) {
            try {
                mOrder = ParametersUtils.toInt(style);
                if (mParent != null && mOrder != -1) {
                    mParent.onChangeChildOrder();
                }
            } catch (IllegalArgumentException e) {
                HNLog.e(HNLog.DOM, "Wrong when read order, expecting integer while actual is " +
                        style +
                        ", " + style.getClass().toString());
            }
        }
        mInlineStyle.put(this, styleName, style);
    }

    private void onChangeChildOrder() {
        if (mIsInOrder) {
            mIsInOrder = false;
        }
    }

    public void appendText(String text) {
        if (mInnerText == null) {
            mInnerText = text;
        } else {
            mInnerText += text;
        }
    }

    public void addChild(HNDomTree child) {
        if (child.mOrder != -1) {
            if (mIsInOrder) {
                mIsInOrder = false;
            }
        }
        mChildren.add(child);
    }

    public boolean isLeaf() {
        return mChildren.isEmpty();
    }

    public boolean isContainer() {
        return !isLeaf();
    }

    public int childrenCount() {
        return mChildren.size();
    }


    private void walkThrough(WalkAction action) {
        this.walkThroughInternal(action, mDepth);
    }

    private void walkThroughInternal(@Nullable WalkAction action, int depth) {
        if (action != null) {
            action.act(this, depth);
        }

        Iterator<HNDomTree> itr = mChildren.iterator();

        while (itr.hasNext()) {
            HNDomTree child = itr.next();
            child.walkThroughInternal(action, this.mDepth + 1);
        }

    }

    @Override
    public List<HNDomTree> children() {
        sortChildrenIfNecessary();
        return mChildren;
    }

    private void sortChildrenIfNecessary() {
        if (!mIsInOrder) {
            Collections.sort(mChildren, DEFAULT_TREE_COMPARATOR);
            mIsInOrder = true;
        }

    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    @Override
    public String getInner() {
        return mInnerText;
    }

    @Override
    public void setType(String type) {
        this.mType = type;
    }

    public int getDepth() {
        return mDepth;
    }

    public HNDomTree last() {
        return mChildren.getLast();
    }


    public String wholeTreeToString() {
        final StringBuilder sb = new StringBuilder();
        this.walkThrough(new WalkAction() {
            @Override
            public void act(HNDomTree node, int depth) {
                for (int i = 0; i < depth; i++) {
                    sb.append("--");
                }
                sb.append(node);
                sb.append('\n');
            }
        });

        return sb.toString();
    }

    @Override
    public void onStartParse() {

    }

    @Override
    public void onLeaveParse() {
        if (mInnerText != null) {
            mInlineStyle.put(this, "text", mInnerText);
        }
    }

    @NonNull
    @Override
    public String toString() {
        String index = "@" + mIndex + ":" + mOrder + ", ";
        String text = (mInnerText == null ? "" : ", text=" + mInnerText);
        return "[" + index + mType + ", attrs=" + mInlineStyle.toString(this) + text + "]";
    }

    public HNDomTree getParent() {
        return mParent;
    }

    @Override
    public boolean hasClazz() {
        return mClass != null && mClass.length > 0;
    }

    @Override
    public boolean hasId() {
        return !TextUtils.isEmpty(mId);
    }

    @Override
    public int attrIndex() {
        return mAttrIndex;
    }

    @Override
    public void setAttrIndex(int newIndex) {
        mAttrIndex = newIndex;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    @Override
    public void setParent(DomElement parent) {
        mParent = (HNDomTree) parent;
    }

    public String[] getClazz() {
        return mClass;
    }

    public void setClazz(String[] clazz) {
        this.mClass = clazz;
    }

    interface WalkAction {
        void act(HNDomTree node, int depth);
    }

    private static class RVDomTreeComparator implements Comparator<HNDomTree> {

        @Override
        public int compare(HNDomTree o1, HNDomTree o2) {
            return o1.mOrder - o2.mOrder;
        }
    }

    private final static RVDomTreeComparator DEFAULT_TREE_COMPARATOR = new RVDomTreeComparator();
}
