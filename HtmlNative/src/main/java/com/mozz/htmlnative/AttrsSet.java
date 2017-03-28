package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;

import com.mozz.htmlnative.attrs.Attr;
import com.mozz.htmlnative.attrs.BackgroundStyle;
import com.mozz.htmlnative.attrs.LayoutAttr;
import com.mozz.htmlnative.common.Utils;
import com.mozz.htmlnative.view.ViewImageAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangTao7
 *         NOT THREAD SAFE
 */

public final class AttrsSet {

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
    private static final String ATTR_CLAZZ = "class";
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

    // for temp usage.

    // to store the width, height, top and left during attribute process
    private int[] mTempPos;

    AttrsSet() {
        this(10);
    }

    AttrsSet(int initCompacity) {
        mAttrs = new Object[initCompacity << 1];
        mLength = new int[initCompacity];
        mGrowLength = 0;
        mCompacity = initCompacity;
        mTempPos = new int[4];
    }

    void put(@NonNull AttrsOwner tree, String paramsKey, @NonNull Object value) {
        int startPosition = tree.attrIndex();

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

    void newAttr(@NonNull AttrsOwner tree) {
        if (mLastGrowLength == mGrowLength) {
            mGrowLength++;
        }

        if (mGrowLength >= mCompacity) {
            grow(mCompacity);
        }

        tree.setAttrIndex(mGrowLength);
        mLastGrowLength = mGrowLength;
    }

    @Override
    public String toString() {
        return Arrays.toString(mAttrs);
    }

    public String toString(@NonNull AttrsOwner tree) {
        int startPos = tree.attrIndex();
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
                      @NonNull AttrsOwner tree, String innerText, String tagName, @NonNull
                              ViewGroup parent, @NonNull ViewGroup.LayoutParams layoutParams,
                      HNRenderer.ViewCreateResult outResult) throws AttrApplyException {


        int startPosition = tree.attrIndex();
        int treeAttrLength = mLength[startPosition];

        // pos = {width, height, left, top}
        mTempPos[0] = ViewGroup.LayoutParams.WRAP_CONTENT;
        mTempPos[1] = ViewGroup.LayoutParams.WRAP_CONTENT;
        mTempPos[2] = 0;
        mTempPos[3] = 0;

        // clear the value in result
        outResult.clazz = null;
        outResult.id = null;

        Attr viewAttr = getAttr(v.getClass());
        Attr extraAttr = AttrsHelper.getExtraAttrFromView(v.getClass());
        Attr parentAttr = getAttr(parent.getClass());
        LayoutAttr parentLayoutAttr = null;
        if (parentAttr instanceof LayoutAttr) {
            parentLayoutAttr = (LayoutAttr) parentAttr;
        }

        // Apply the default attr to view first;
        // Then process each parameter.

        applyDefault(context, tagName, sandBoxContext, v, innerText, parent, layoutParams,
                mTempPos, viewAttr, extraAttr, parentLayoutAttr);

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            applyAttrToView(context, tagName, sandBoxContext, v, (String) mAttrs[i << 1], mAttrs[
                    (i << 1) + 1], innerText, parent, layoutParams, mTempPos, viewAttr,
                    extraAttr, parentLayoutAttr, outResult);
        }


        layoutParams.height = mTempPos[1];
        layoutParams.width = mTempPos[0];

        if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
            ((AbsoluteLayout.LayoutParams) layoutParams).x = mTempPos[2];
            ((AbsoluteLayout.LayoutParams) layoutParams).y = mTempPos[3];
        }
    }

    public static Attr getViewAttr(View v) {
        return getAttr(v.getClass());
    }

    public static Attr getExtraAttr(View v) {
        return AttrsHelper.getExtraAttrFromView(v.getClass());
    }

    public static LayoutAttr getParentAttr(ViewParent parent) {
        if (parent instanceof ViewGroup) {
            Attr parentAttr = getAttr(((ViewGroup) parent).getClass());
            LayoutAttr parentLayoutAttr = null;
            if (parentAttr instanceof LayoutAttr) {
                parentLayoutAttr = (LayoutAttr) parentAttr;
            }
            return parentLayoutAttr;
        }
        return null;
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
            viewAttr, Attr extralAttr, LayoutAttr parentAttr, HNRenderer.ViewCreateResult
            outResult) throws AttrApplyException {


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
                if (value instanceof BackgroundStyle) {
                    BackgroundStyle backgroundStyle = (BackgroundStyle) value;

                    if (!TextUtils.isEmpty(backgroundStyle.getUrl())) {
                        HNRenderer.getImageViewAdpater().setImage(backgroundStyle.getUrl(), new
                                ViewImageAdapter(v));
                    } else if (backgroundStyle.isColorSet()) {
                        v.setBackgroundColor(backgroundStyle.getColor());
                    }
                } else {
                    throw new AttrApplyException("Background style is wrong when parsing.");
                }

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
                    if (outResult != null) {
                        outResult.id = (String) value;
                    }
                } else {
                    throw new AttrApplyException("id must be a string.");
                }
                break;

            case ATTR_CLAZZ:
                if (value instanceof String) {
                    if (outResult != null) {
                        outResult.clazz = (String) value;
                    }
                } else {
                    throw new AttrApplyException("class must be a string.");
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
            name, @NonNull AttrsOwner tree) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {


        int startPosition = tree.attrIndex();
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
