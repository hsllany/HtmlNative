package com.mozz.remoteview.parser;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


final class RVDomTree {

    private static final String TAG = RVDomTree.class.getSimpleName();

    private static boolean DEBUG = false;

    private int mDepth;

    private RVDomTree mParent;
    private int mIndex;
    List<RVDomTree> mChildren;
    String mNodeName;
    AttrsSet mAttrs;

    RVModule mContext;

    // for cache use
    int mBracketPair;
    int mTagPair;


    RVDomTree(@NonNull RVModule context, RVDomTree parent, int depth, int index) {
        this(context, null, parent, depth, index);
    }

    private RVDomTree(@NonNull RVModule context, String nodeName, RVDomTree parent, int depth, int index) {
        mContext = context;
        mNodeName = nodeName;
        mDepth = depth;
        mParent = parent;
        mIndex = index;
        mChildren = new LinkedList<>();
        mAttrs = new AttrsSet(context);
    }

    void addAttr(String attrName, String value) {
        mAttrs.put(attrName, value);
    }

    void addAttr(String attrName, double value) {
        mAttrs.put(attrName, value);
    }

    void addAttr(String attrName, int value) {
        mAttrs.put(attrName, value);
    }

    RVDomTree addChild(String nodeName, int index) {
        RVDomTree child = new RVDomTree(mContext, nodeName, this, this.mDepth + 1, index);
        if (DEBUG) {
            Log.d(TAG, "add child " + child.toString() + " to " + this.toString() + ".");
        }
        mChildren.add(child);
        return child;
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

    private void walkThroughInternal(WalkAction action, int depth) {
        if (action != null)
            action.act(this, depth);

        Iterator<RVDomTree> itr = mChildren.iterator();

        while (itr.hasNext()) {
            RVDomTree child = itr.next();
            child.walkThroughInternal(action, this.mDepth + 1);
        }

    }

    String getNodeName() {
        return mNodeName;
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
    public String toString() {
        String index = "@" + mIndex + ", ";
        return "[" + index + mNodeName + ", attrs=" + mAttrs.toString() + "]";
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
}
