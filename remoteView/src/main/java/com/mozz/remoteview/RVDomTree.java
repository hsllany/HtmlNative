package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mozz.remoteview.common.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


final class RVDomTree implements RVDomElement, Parser.ParseCallback {

    static final String INNER_TREE_TAG = "inner";

    private static final String TREE_ORDER_PARAMETER = "order";

    private static final String TAG = RVDomTree.class.getSimpleName();

    static boolean DEBUG = false;

    private int mDepth;

    private RVDomTree mParent;

    /**
     * Represent the appearance position in RV file.
     * Notice this is not the actual position in tree's children. See {@link RVDomTree#mOrder}
     */
    private int mIndex;

    private LinkedList<RVDomTree> mChildren;

    @Nullable
    String mNodeName;

    private RVSegment mModule;

    @Nullable
    private String mText = null;

    int mAttrIndex;

    // for cache use
    int mBracketPair;

    int mTagPair;

    private int mOrder = -1;

    /**
     * Mark whether a tree's children are in order. Set default true, being set to false only when
     * a tree with mOrder!=-1 has been added as child. {@link RVDomTree#addChild(RVDomTree)}
     */
    private boolean mIsInOrder = true;

    RVDomTree(@NonNull RVSegment context, RVDomTree parent, int depth, int index) {
        this(context, null, parent, depth, index);
    }

    private RVDomTree(@NonNull RVSegment module, String nodeName, RVDomTree parent, int depth, int
            index) {
        mModule = module;
        mNodeName = nodeName;
        mDepth = depth;
        mParent = parent;
        mIndex = index;
        mChildren = new LinkedList<>();

        module.mAttrs.newAttr(this);
    }

    RVDomTree(@NonNull RVDomTree parent, String nodeName, int index) {
        this(parent.mModule, nodeName, parent, parent.mDepth + 1, index);
    }

    void addAttr(String attrName, @NonNull Object value) {
        if (TREE_ORDER_PARAMETER.equalsIgnoreCase(attrName)) {
            try {
                mOrder = Utils.toInt(value);
                if (mParent != null && mOrder != -1) {
                    mParent.onChangeChildOrder();
                }
            } catch (AttrApplyException e) {
                Log.i(TAG, "Wrong when read order, expecting integer while actual is " + value +
                        ", " + value.getClass().toString());
            }
        }
        mModule.mAttrs.put(this, attrName, value);
    }

    private void onChangeChildOrder() {
        if (mIsInOrder) {
            mIsInOrder = false;
        }
    }

    void appendText(String text) {
        if (mText == null) {
            mText = text;
        } else {
            mText += text;
        }
    }

    void addChild(RVDomTree child) {
        if (DEBUG) {
            Log.d(TAG, "add child " + child.toString() + " to " + this.toString() + ".");
        }
        if (child.mOrder != -1) {
            if (mIsInOrder) {
                mIsInOrder = false;
            }
        }
        mChildren.add(child);
    }

    boolean isLeaf() {
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

        Iterator<RVDomTree> itr = mChildren.iterator();

        while (itr.hasNext()) {
            RVDomTree child = itr.next();
            child.walkThroughInternal(action, this.mDepth + 1);
        }

    }

    List<RVDomTree> children() {
        sortChildrenIfNecessary();
        return mChildren;
    }

    private void sortChildrenIfNecessary() {
        if (!mIsInOrder) {
            Collections.sort(mChildren, DEFAULT_RVTREE_COMPARATOR);
            mIsInOrder = true;
        }

    }

    @Nullable
    @Override
    public String getNodeName() {
        return mNodeName;
    }

    @Nullable
    @Override
    public String getInner() {
        return mText;
    }

    public int getDepth() {
        return mDepth;
    }

    RVDomTree last() {
        return mChildren.getLast();
    }


    String wholeTreeToString() {
        final StringBuilder sb = new StringBuilder();
        this.walkThrough(new WalkAction() {
            @Override
            public void act(RVDomTree node, int depth) {
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
        if (mText != null) {
            mModule.mAttrs.put(this, "text", mText);
        }
    }

    @NonNull
    @Override
    public String toString() {
        String index = "@" + mIndex + ":" + mOrder + ", ";
        String text = (mText == null ? "" : ", text=" + mText);
        return "[" + index + mNodeName + ", attrs=" + mModule.mAttrs.toString(this) + text + "]";
    }

    public RVDomTree getParent() {
        return mParent;
    }

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }

    interface WalkAction {
        void act(RVDomTree node, int depth);
    }

    private static class RVDomTreeComparator implements Comparator<RVDomTree> {

        @Override
        public int compare(RVDomTree o1, RVDomTree o2) {
            return o1.mOrder - o2.mOrder;
        }
    }

    private final static RVDomTreeComparator DEFAULT_RVTREE_COMPARATOR = new RVDomTreeComparator();
}
