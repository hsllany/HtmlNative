package com.mozz.remoteview;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public final class RVDomTree {

    static final String INNER_TREE_TAG = "inner";

    private static final String TAG = RVDomTree.class.getSimpleName();

    static boolean DEBUG = false;

    private int mDepth;

    private RVDomTree mParent;

    private int mIndex;

    List<RVDomTree> mChildren;

    String mNodeName;

    private RVModule mModule;

    private String mText = null;

    int mAttrIndex;

    // for cache use
    int mBracketPair;

    int mTagPair;

    RVDomTree(@NonNull RVModule context, RVDomTree parent, int depth, int index) {
        this(context, null, parent, depth, index);
    }

    private RVDomTree(@NonNull RVModule module, String nodeName, RVDomTree parent, int depth, int index) {
        mModule = module;
        mNodeName = nodeName;
        mDepth = depth;
        mParent = parent;
        mIndex = index;
        mChildren = new LinkedList<>();

        module.mAttrs.newAttr(this);
    }

    void addAttr(String attrName, Object value) {
        mModule.mAttrs.put(this, attrName, value);
    }

    void appendText(String text) {
        if (mText == null)
            mText = text;
        else
            mText += text;
    }

    RVDomTree addChild(String nodeName, int index) {
        RVDomTree child = new RVDomTree(mModule, nodeName, this, this.mDepth + 1, index);
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

    public String getInner() {
        return mText;
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
}
