package com.mozz.remoteview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozz.remoteview.attrs.Attr;
import com.mozz.remoteview.attrs.ImageViewAttr;
import com.mozz.remoteview.attrs.LinearLayoutAttr;
import com.mozz.remoteview.attrs.TextViewAttr;
import com.mozz.remoteview.code.Code;

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
    private static final String ATTR_ALPHA = "alpha";
    private static final String ATTR_ID = "id";
    private static final String ATTR_ONCLICK = "onClick";

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

    public void apply(Context context, final ViewContext viewContext, View v, RVDomTree tree, ViewGroup.LayoutParams layoutParams) throws AttrApplyException {
        int startPosition = tree.mAttrIndex;
        int treeAttrLength = mLength[startPosition];

        if (v instanceof LinearLayout)
            ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);


        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        for (int i = startPosition; i < startPosition + treeAttrLength; i++) {

            String params = (String) mAttrs[i << 1];
            final Object value = mAttrs[(i << 1) + 1];

            if (DEBUG) {
                Log.i(TAG, "ready to parse attribute " + params + " with value " + value + ", for view " + v);
            }

            if (params.equals(ATTR_WIDTH)) {
                if (value instanceof Integer) {
                    width = (Integer) value;
                } else if (value.toString().equalsIgnoreCase("MATCH_PARENT")) {
                    width = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    throw new AttrApplyException("Width must be an int or 'WRAP_CONTENT'");
                }

            } else if (params.equals(ATTR_HEIGHT)) {
                if (value instanceof Integer) {
                    height = (Integer) value;
                } else if (value.toString().equalsIgnoreCase("MATCH_PARENT")) {
                    height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    throw new AttrApplyException("Height must be an int or 'WRAP_CONTENT'");
                }

            } else if (params.equals(ATTR_BACKGROUND)) {
                try {
                    int backgroundColor = Color.parseColor(value.toString());
                    v.setBackgroundColor(backgroundColor);
                } catch (IllegalArgumentException e) {
                    AttrApplyException eThrow = new AttrApplyException("color parse wrong!");
                    eThrow.initCause(e);
                    throw eThrow;
                }

            } else if (params.equals(ATTR_PADDING)) {
                if (value instanceof Integer) {
                    int padding = (int) value;

                    v.setPadding(padding, padding, padding, padding);
                }

            } else if (params.equals(ATTR_PADDING_LEFT)) {
                if (value instanceof Integer) {
                    v.setPadding((int) value, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
                }

            } else if (params.equals(ATTR_PADDING_RIGHT)) {
                if (value instanceof Integer) {
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), (int) value, v.getPaddingBottom());
                }

            } else if (params.equals(ATTR_PADDING_TOP)) {
                if (value instanceof Integer) {
                    v.setPadding(v.getPaddingLeft(), (int) value, v.getPaddingRight(), v.getPaddingBottom());
                }

            } else if (params.equals(ATTR_PADDING_BOTTOM)) {
                if (value instanceof Integer) {
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), (int) value);
                }

            } else if (params.equals(ATTR_ALPHA)) {
                if (value instanceof Double) {
                    double d = (double) value;
                    v.setAlpha((float) d);
                }
            } else if (params.equals(ATTR_ID)) {
                if (value instanceof String) {
                    viewContext.put((String) value, v);
                } else {
                    throw new AttrApplyException("id must be a string.");
                }
            } else if (params.equals(ATTR_ONCLICK)) {

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
            } else {
                Attr attr = sCachedAttrs.get(v.getClass());
                if (attr == null) {
                    attr = getAttrFromView(v.getClass());
                    if (attr != null) {
                        sCachedAttrs.put(v.getClass(), attr);
                    }
                }

                if (attr != null) {
                    attr.apply(context, v, params, value);
                }
            }


        }

        layoutParams.height = height;
        layoutParams.width = width;
    }

    public static void toggleDebug(boolean debug) {
        DEBUG = debug;
    }

    //TODO there is much can be done when dealing with the Attr
    private static Attr getAttrFromView(Class<? extends View> clazz) {
        if (clazz.equals(TextView.class) || clazz.equals(Button.class)) {
            return new TextViewAttr();
        } else if (clazz.equals(ImageView.class)) {
            return new ImageViewAttr();
        } else if (clazz.equals(LinearLayout.class)) {
            return new LinearLayoutAttr();
        } else {
            return null;
        }
    }

    public static class AttrApplyException extends Exception {
        public AttrApplyException(String msg) {
            super(msg);
        }

    }

}
