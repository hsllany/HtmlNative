package com.mozz.remoteview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.mozz.remoteview.attrs.Attr;
import com.mozz.remoteview.attrs.FlexboxLayoutAttr;
import com.mozz.remoteview.attrs.ImageViewAttr;
import com.mozz.remoteview.attrs.LayoutAttr;
import com.mozz.remoteview.attrs.LinearLayoutAttr;
import com.mozz.remoteview.attrs.TextViewAttr;
import com.mozz.remoteview.common.Utils;
import com.mozz.remoteview.script.Code;

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
    static boolean DEBUG = false;

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
    private static final String ATTR_VISIBLE = "visible";

    private static final String ATTR_DISPLAY = "display";

    private Object[] mAttrs;

    private int[] mLength;

    private int mGrowLength;

    private int mLastGrowLength = -1;

    private int mCompacity;

    private RVModule mModule;

    private static Map<Class<? extends View>, Attr> sCachedAttrs = new HashMap<>();

    AttrsSet(@NonNull RVModule context) {
        this(context, 10);
    }

    AttrsSet(RVModule module, int initCompacity) {
        mModule = module;
        mAttrs = new Object[initCompacity << 1];
        mLength = new int[initCompacity];
        mGrowLength = 0;
        mCompacity = initCompacity;
    }

    void put(RVDomTree tree, String paramsKey, Object value) {
        int startPosition = tree.mAttrIndex;

        if (DEBUG) {
            Log.d(TAG, "put " + paramsKey + ", " + value.toString() + " in attrs at " + (startPosition + mLength[startPosition]));
        }

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
            if (DEBUG) {
                Log.i(TAG, " grow to " + (mCompacity + growSize));
            }
            Object[] temp = mAttrs;
            int[] tempL = mLength;

            mAttrs = new Object[(mCompacity + growSize) << 1];
            mLength = new int[mCompacity + growSize];

            System.arraycopy(temp, 0, mAttrs, 0, mCompacity << 1);
            System.arraycopy(tempL, 0, mLength, 0, mCompacity);

            mCompacity += growSize;
        }
    }

    void newAttr(RVDomTree tree) {
        if (mLastGrowLength == mGrowLength) {
            mGrowLength++;
        }

        if (mGrowLength >= mCompacity) {
            grow(mCompacity);
        }

        if (DEBUG) {
            Log.d(TAG, "give tree" + tree.toString() + " index at " + mGrowLength);
        }
        tree.mAttrIndex = mGrowLength;
        mLastGrowLength = mGrowLength;
    }


    @Override
    public String toString() {
        return Arrays.toString(mAttrs);
    }

    public String toString(RVDomTree tree) {
        int startPos = tree.mAttrIndex;
        int length = mLength[startPos];

        Object[] objects = new Object[length << 1];
        System.arraycopy(mAttrs, startPos << 1, objects, 0, length << 1);

        return Arrays.toString(objects);
    }

    @SuppressWarnings("ConstantConditions")
    public void apply(Context context, final ViewContext viewContext, View v, RVDomTree tree,
                      ViewGroup parent, ViewGroup.LayoutParams layoutParams)
            throws AttrApplyException {

        int startPosition = tree.mAttrIndex;
        int treeAttrLength = mLength[startPosition];

        if (v instanceof LinearLayout)
            ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);


        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        int left = 0;
        int top = 0;

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {

            String params = (String) mAttrs[i << 1];
            final Object value = mAttrs[(i << 1) + 1];

            if (DEBUG) {
                Log.i(TAG, "ready to parse attribute " + params + " with value " + value + ", for view " + v);
            }

            switch (params) {
                case ATTR_WIDTH:
                    if (value instanceof Integer) {
                        width = (Integer) value;
                    } else if (value.toString().equalsIgnoreCase("MATCH_PARENT")) {
                        width = ViewGroup.LayoutParams.MATCH_PARENT;
                    } else {
                        throw new AttrApplyException("Width must be an int or 'WRAP_CONTENT'");
                    }

                    break;

                case ATTR_HEIGHT:
                    if (value instanceof Integer) {
                        height = (Integer) value;
                    } else if (value.toString().equalsIgnoreCase("MATCH_PARENT")) {
                        height = ViewGroup.LayoutParams.MATCH_PARENT;
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
                    v.setPadding(paddingLeft, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
                    break;

                case ATTR_PADDING_RIGHT:
                    int paddingRight = Utils.toInt(value);
                    v.setPadding(v.getPaddingTop(), v.getPaddingTop(), paddingRight, v.getPaddingBottom());
                    break;

                case ATTR_PADDING_TOP:
                    int paddingTop = Utils.toInt(value);
                    v.setPadding(v.getPaddingLeft(), paddingTop, v.getPaddingRight(), v.getPaddingBottom());
                    break;

                case ATTR_PADDING_BOTTOM:
                    int paddingBottom = Utils.toInt(value);
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), paddingBottom);
                    break;

                case ATTR_LEFT:
                    left = Utils.toInt(value);
                    break;

                case ATTR_TOP:
                    top = Utils.toInt(value);
                    break;

                case ATTR_ALPHA:
                    float alpha = Utils.toFloat(value);
                    v.setAlpha(alpha);
                    break;

                case ATTR_ID:
                    if (value instanceof String) {
                        viewContext.put((String) value, v);
                    } else {
                        throw new AttrApplyException("id must be a string.");
                    }
                    break;

                case ATTR_VISIBLE:
                    boolean visible = Utils.toBoolean(value);
                    v.setVisibility(visible ? View.VISIBLE : View.GONE);

                case ATTR_ONCLICK:

                    if (value instanceof String) {
                        final String functionName = (String) value;
                        final Code code = mModule.retrieveCode(functionName);

                        if (code != null) {
                            v.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    code.execute(viewContext);
                                }
                            });
                        } else {
                            Log.w(TAG, "Can't find related function " + functionName);
                        }
                    }
                    break;

                default:
                    Attr attr = getAttr(v.getClass());

                    if (attr != null) {
                        attr.apply(context, v, params, value, tree);
                    }

                    // If there extra attr is set, then should be applied also.
                    attr = getExtraAttrFromView(v.getClass());
                    if (attr != null) {
                        attr.apply(context, v, params, value, tree);
                    }

                    // finally apply corresponding parent attr to child
                    attr = getAttr(parent.getClass());
                    if (attr != null && attr instanceof LayoutAttr) {
                        ((LayoutAttr) attr).applyToChild(context, v, params, value);
                    }
                    break;
            }


        }

        layoutParams.height = height;
        layoutParams.width = width;

        if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
            ((AbsoluteLayout.LayoutParams) layoutParams).x = left;
            ((AbsoluteLayout.LayoutParams) layoutParams).y = top;
        }
    }

    View createViewViaAttr(RVRenderer renderer, Context context, String name, RVDomTree tree)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        int startPosition = tree.mAttrIndex;
        int treeAttrLength = mLength[startPosition];

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {
            String params = (String) mAttrs[i << 1];
            final Object value = mAttrs[(i << 1) + 1];

            switch (params) {
                case ATTR_DISPLAY:
                    if (name.equals(HtmlTag.DIV)) {
                        if (value.equals("flex")) {
                            return renderer.createView(context, ViewRegistry.findClassByTag("flexbox"));
                        } else if (value.equals("absolute")) {
                            return renderer.createView(context, ViewRegistry.findClassByTag("box"));
                        } else if (value.equals("box")) {
                            return renderer.createView(context, ViewRegistry.findClassByTag("linearbox"));
                        }
                    }
                    break;
            }
        }

        return renderer.createView(context, ViewRegistry.findClassByTag("linearbox"));

    }

    private Attr getAttr(Class<? extends View> clazz) {
        Attr attr = sCachedAttrs.get(clazz);
        if (attr == null) {
            attr = getAttrFromView(clazz);
            if (attr != null) {
                sCachedAttrs.put(clazz, attr);
            }
        }

        return attr;
    }

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }

    //TODO there is much can be done when dealing with the Attr
    private static Attr getAttrFromView(Class<? extends View> clazz) {
        // cover all TextView sub classes
        if (TextView.class.isAssignableFrom(clazz)) {
            return TextViewAttr.getInstance();

        } else if (clazz.equals(ImageView.class)) {
            return new ImageViewAttr();
        } else if (clazz.equals(LinearLayout.class)) {
            return new LinearLayoutAttr();
        } else if (clazz.equals(FlexboxLayout.class)) {
            return new FlexboxLayoutAttr();
        } else {
            return null;
        }
    }

    @Nullable
    private static Attr getExtraAttrFromView(@NonNull Class<? extends View> clazz) {
        return ViewRegistry.findAttrFromExtraByTag(clazz.getName());
    }

}
