package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.mozz.htmlnative.attrs.Attr;
import com.mozz.htmlnative.attrs.LayoutAttr;
import com.mozz.htmlnative.common.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangTao7
 *         NOT THREAD SAFE
 */

final class AttrsSet {

    private static final String TAG = AttrsSet.class.getSimpleName();

    private static final String ATTR_WIDTH = "width";
    private static final String ATTR_HEIGHT = "height";
    private static final String ATTR_BACKGROUND = "background";
    private static final String ATTR_PADDING = "padding";
    private static final String ATTR_PADDING_LEFT = "paddingLeft";
    private static final String ATTR_PADDING_RIGHT = "paddingRight";
    private static final String ATTR_PADDING_TOP = "paddingTop";
    private static final String ATTR_PADDING_BOTTOM = "paddingBottom";
    private static final String ATTR_LEFT = "left";
    private static final String ATTR_TOP = "top";
    private static final String ATTR_ALPHA = "alpha";
    private static final String ATTR_ID = "id";
    private static final String ATTR_ONCLICK = "onClick";
    private static final String ATTR_VISIBLE = "visibility";
    private static final String ATTR_DISPLAY = "display";
    private static final String ATTR_DIRECTION = "direction";
    private static final String VAL_FILL_PARENT = "100%";

    @NonNull
    private static Map<Class<? extends View>, Attr> sCachedAttrs = new HashMap<>();

    private Object[] mAttrs;
    private int[] mLength;
    private int mGrowLength;
    private int mLastGrowLength = -1;
    private int mCompacity;

    AttrsSet() {
        this(10);
    }

    AttrsSet(int initCompacity) {
        mAttrs = new Object[initCompacity << 1];
        mLength = new int[initCompacity];
        mGrowLength = 0;
        mCompacity = initCompacity;
    }

    void put(@NonNull HNDomTree tree, String paramsKey, @NonNull Object value) {
        int startPosition = tree.mAttrIndex;

        putInternal(startPosition + mLength[startPosition], paramsKey, value);
        mLength[startPosition]++;
    }

    private void putInternal(int position, String paramsKey, Object value) {
        if (position >= mCompacity) {
            grow(mCompacity);
        }

        mAttrs[position << 1] = paramsKey;
        mAttrs[(position << 1) + 1] = value;
        mGrowLength++;
    }

    private void grow(int growSize) {
        if (growSize > 0) {
            Object[] temp = mAttrs;
            int[] tempL = mLength;

            mAttrs = new Object[(mCompacity + growSize) << 1];
            mLength = new int[mCompacity + growSize];

            System.arraycopy(temp, 0, mAttrs, 0, mCompacity << 1);
            System.arraycopy(tempL, 0, mLength, 0, mCompacity);

            mCompacity += growSize;
        }
    }

    void newAttr(@NonNull HNDomTree tree) {
        if (mLastGrowLength == mGrowLength) {
            mGrowLength++;
        }

        if (mGrowLength >= mCompacity) {
            grow(mCompacity);
        }

        tree.mAttrIndex = mGrowLength;
        mLastGrowLength = mGrowLength;
    }

    @Override
    public String toString() {
        return Arrays.toString(mAttrs);
    }

    public String toString(@NonNull HNDomTree tree) {
        int startPos = tree.mAttrIndex;
        int length = mLength[startPos];

        Object[] objects = new Object[length << 1];
        System.arraycopy(mAttrs, startPos << 1, objects, 0, length << 1);

        return Arrays.toString(objects);
    }

    /**
     * apply the attr to the view
     *
     * @param context        {@link android.content.Context}
     * @param sandBoxContext {@link HNSandBoxContext}
     * @param v              {@link android.view.View}
     * @param tree           {@link HNDomTree}
     * @param parent         {@link android.view.ViewGroup}, parent of the view
     * @param layoutParams   {@link android.view.ViewGroup.LayoutParams}, layoutParams for parent
     *                       when add this view to parent
     * @throws AttrApplyException
     */
    public void apply(Context context, @NonNull final HNSandBoxContext sandBoxContext, View v,
                      @NonNull HNDomTree tree, @NonNull ViewGroup parent, @NonNull ViewGroup
            .LayoutParams layoutParams) throws AttrApplyException {

        String tagName = tree.getTag();

        int startPosition = tree.mAttrIndex;
        int treeAttrLength = mLength[startPosition];

        // pos = {width, height, left, top}
        int[] outPos = {ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                0, 0};

        Attr viewAttr = getAttr(v.getClass());
        Attr extraAttr = AttrsHelper.getExtraAttrFromView(v.getClass());
        Attr parentAttr = getAttr(parent.getClass());
        LayoutAttr parentLayoutAttr = null;
        if (parentAttr instanceof LayoutAttr) {
            parentLayoutAttr = (LayoutAttr) parentAttr;
        }

        // Apply the default attr to view first;
        // Then process each parameter.

        applyDefault(context, tagName, sandBoxContext, v, tree.getInner(), parent, layoutParams,
                outPos, viewAttr, extraAttr, parentLayoutAttr);

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            applyAttrToView(context, tagName, sandBoxContext, v, (String) mAttrs[i << 1], mAttrs[
                    (i << 1) + 1], tree.getInner(), parent, layoutParams, outPos, viewAttr,
                    extraAttr, parentLayoutAttr);
        }


        layoutParams.height = outPos[1];
        layoutParams.width = outPos[0];

        if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
            ((AbsoluteLayout.LayoutParams) layoutParams).x = outPos[2];
            ((AbsoluteLayout.LayoutParams) layoutParams).y = outPos[3];
        }
    }

    /**
     * Apply a params with value to a view
     *
     * @param context        {@link android.content.Context}
     * @param tagName        TagName of this view in raw Dom
     * @param sandBoxContext {@link HNSandBoxContext}
     * @param v              {@link android.view.View} view to be processed
     * @param params         parameter name
     * @param value          parameter value
     * @param innerElement   inner String in this tag element
     * @param parent         {@link android.view.ViewGroup}, parent of the view
     * @param layoutParams   {@link android.view.ViewGroup.LayoutParams}, layoutParams for parent
     *                       when add this view to parent
     * @param posOut         int array, {width, height, left, top} will be set after this function
     * @throws AttrApplyException
     */
    public static void applyAttrToView(Context context, String tagName, final HNSandBoxContext
            sandBoxContext, View v, String params, Object value, String innerElement, @NonNull
            ViewGroup parent, @NonNull ViewGroup.LayoutParams layoutParams, int[] posOut, Attr
            viewAttr, Attr extralAttr, LayoutAttr parentAttr) throws AttrApplyException {


        switch (params) {
            case ATTR_WIDTH:
                if (value instanceof Integer) {
                    posOut[0] = (Integer) value;
                } else if (value.toString().equalsIgnoreCase(VAL_FILL_PARENT)) {
                    posOut[0] = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    throw new AttrApplyException("Width must be an int or 'WRAP_CONTENT'");
                }

                break;

            case ATTR_HEIGHT:
                if (value instanceof Integer) {
                    posOut[1] = (Integer) value;
                } else if (value.toString().equalsIgnoreCase(VAL_FILL_PARENT)) {
                    posOut[1] = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    throw new AttrApplyException("Height must be an int or 'WRAP_CONTENT'");
                }

                break;

            case ATTR_BACKGROUND:
                v.setBackgroundColor(Utils.color(value));
                break;

            case ATTR_PADDING:
                int padding = Utils.toInt(value);
                v.setPadding(padding, padding, padding, padding);
                break;

            case ATTR_PADDING_LEFT:
                int paddingLeft = Utils.toInt(value);
                v.setPadding(paddingLeft, v.getPaddingTop(), v.getPaddingRight(), v
                        .getPaddingBottom());
                break;

            case ATTR_PADDING_RIGHT:
                int paddingRight = Utils.toInt(value);
                v.setPadding(v.getPaddingTop(), v.getPaddingTop(), paddingRight, v
                        .getPaddingBottom());
                break;

            case ATTR_PADDING_TOP:
                int paddingTop = Utils.toInt(value);
                v.setPadding(v.getPaddingLeft(), paddingTop, v.getPaddingRight(), v
                        .getPaddingBottom());
                break;

            case ATTR_PADDING_BOTTOM:
                int paddingBottom = Utils.toInt(value);
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(),
                        paddingBottom);
                break;

            case ATTR_LEFT:
                posOut[2] = Utils.toInt(value);
                break;

            case ATTR_TOP:
                posOut[3] = Utils.toInt(value);
                break;

            case ATTR_ALPHA:
                float alpha = Utils.toFloat(value);
                v.setAlpha(alpha);
                break;

            case ATTR_ID:
                if (value instanceof String) {
                    sandBoxContext.saveId((String) value, v);
                } else {
                    throw new AttrApplyException("id must be a string.");
                }
                break;

            case ATTR_VISIBLE:
                String visible = value.toString();

                if (visible.equals("visible")) {
                    v.setVisibility(View.VISIBLE);
                } else if (visible.equals("invisible")) {
                    v.setVisibility(View.INVISIBLE);
                }
                break;

            case ATTR_DIRECTION:
                String direction = value.toString();

                if (direction.equals("ltr")) {
                    v.setTextDirection(View.TEXT_DIRECTION_LTR);
                } else if (direction.equals("rtl")) {
                    v.setTextDirection(View.TEXT_DIRECTION_RTL);
                }
                break;

            case ATTR_ONCLICK:

                if (value instanceof String) {

                    final String functionName = (String) value;

                    v.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            sandBoxContext.executeFun(functionName);
                        }
                    });

                }
                break;

            default:

                // If not common attrs, then
                // 1. apply the corresponding view attr first;
                // 2. apply the extra attr
                // 3. use parent view attr to this

                if (viewAttr != null) {
                    viewAttr.apply(context, tagName, v, params, value, innerElement);
                }

                // If there extra attr is set, then should be applied also.

                if (extralAttr != null) {
                    extralAttr.apply(context, tagName, v, params, value, innerElement);
                }

                // finally apply corresponding parent attr to child
                if (parentAttr != null) {
                    parentAttr.applyToChild(context, tagName, v, parent, params, value);
                }
                break;
        }

    }

    public static void applyDefault(Context context, String tagName, final HNSandBoxContext
            sandBoxContext, View v, String innerElement, @NonNull ViewGroup parent, @NonNull
            ViewGroup.LayoutParams layoutParams, int[] posOut, Attr viewAttr, Attr extralAttr,
                                    LayoutAttr parentAttr) throws AttrApplyException {
        if (viewAttr != null) {
            viewAttr.setDefault(context, tagName, v, innerElement);
        }

        if (extralAttr != null) {
            extralAttr.setDefault(context, tagName, v, innerElement);
        }

        if (parentAttr != null) {
            parentAttr.setDefaultToChild(context, tagName, v, innerElement);
        }
    }

    final View createViewByTag(@NonNull HNRenderer renderer, Context context, @NonNull String
            name, @NonNull HNDomTree tree) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {


        int startPosition = tree.mAttrIndex;
        int treeAttrLength = mLength[startPosition];

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            String params = (String) mAttrs[i << 1];
            final Object value = mAttrs[(i << 1) + 1];

            switch (params) {
                case ATTR_DISPLAY:
                    if (value.equals("flex")) {
                        return renderer.createViewByTag(context, "flexbox");
                    } else if (value.equals("absolute")) {
                        return renderer.createViewByTag(context, "box");
                    } else if (value.equals("box")) {
                        return renderer.createViewByTag(context, "linearbox");
                    }

                    break;
            }
        }

        return renderer.createViewByTag(context, "linearbox");

    }

    @Nullable
    private static Attr getAttr(@NonNull Class<? extends View> clazz) {
        Attr attr = sCachedAttrs.get(clazz);
        if (attr == null) {
            attr = AttrsHelper.getAttrFromView(clazz);
            if (attr != null) {
                sCachedAttrs.put(clazz, attr);
            }
        }

        return attr;
    }

}
