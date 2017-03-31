package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.mozz.htmlnative.attrs.AttrHandler;
import com.mozz.htmlnative.attrs.BackgroundStyle;
import com.mozz.htmlnative.attrs.LayoutAttrHandler;
import com.mozz.htmlnative.attrs.PixelValue;
import com.mozz.htmlnative.common.Utils;
import com.mozz.htmlnative.view.ViewImageAdapter;

/**
 * @author Yang Tao, 17/3/30.
 */
public final class Styles {

    private static final String TAG = Styles.class.getSimpleName();

    private Styles() {
    }

    static final String ATTR_STYLE = "style";
    static final String ATTR_WIDTH = "width";
    static final String ATTR_HEIGHT = "height";
    static final String ATTR_BACKGROUND = "background";
    static final String ATTR_PADDING = "padding";
    static final String ATTR_PADDING_LEFT = "padding-left";
    static final String ATTR_PADDING_RIGHT = "padding-right";
    static final String ATTR_PADDING_TOP = "padding-top";
    static final String ATTR_PADDING_BOTTOM = "padding-bottom";
    static final String ATTR_MARGIN = "margin";
    static final String ATTR_MARGIN_LEFT = "margin-left";
    static final String ATTR_MARGIN_RIGHT = "margin-right";
    static final String ATTR_MARGIN_TOP = "margin-top";
    static final String ATTR_MARGIN_BOTTOM = "margin-bottom";
    static final String ATTR_LEFT = "left";
    static final String ATTR_TOP = "top";
    static final String ATTR_ALPHA = "alpha";
    static final String ATTR_ID = "id";
    static final String ATTR_CLAZZ = "class";
    static final String ATTR_ONCLICK = "onClick";
    static final String ATTR_VISIBLE = "visibility";
    static final String ATTR_DISPLAY = "display";
    static final String ATTR_DIRECTION = "direction";

    static final String VAL_FILL_PARENT = "100%";

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
     * @throws AttrApplyException
     */
    public static void applyStyle(Context context, String tagName, final HNSandBoxContext
            sandBoxContext, View v, String params, Object value, String innerElement, @NonNull
            ViewGroup parent, @NonNull ViewGroup.LayoutParams layoutParams, AttrHandler
            viewAttrHandler, AttrHandler extraAttrHandler, LayoutAttrHandler parentAttr,
                                  CssIdClass outResult) throws AttrApplyException {


        HNLog.d(HNLog.STYLE, "apply " + params + " = " + value.toString() + " to " + v + "(" +
                tagName + ")");

        switch (params) {
            case ATTR_WIDTH:
                if (value instanceof Integer) {
                    layoutParams.width = (Integer) value;
                } else if (value.toString().equalsIgnoreCase(VAL_FILL_PARENT)) {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    throw new AttrApplyException("Width must be an int or 'WRAP_CONTENT'");
                }

                break;

            case ATTR_HEIGHT:
                if (value instanceof Integer) {
                    layoutParams.height = (Integer) value;
                } else if (value.toString().equalsIgnoreCase(VAL_FILL_PARENT)) {
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
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


            case ATTR_MARGIN: {
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    PixelValue[] pixelValues = Utils.pixelPairs(value.toString());
                    int top = -1;
                    int bottom = -1;
                    int left = -1;
                    int right = -1;
                    if (pixelValues.length == 1) {
                        top = bottom = left = right = (int) pixelValues[0].getValue();
                    } else if (pixelValues.length == 2) {
                        top = bottom = (int) pixelValues[0].getValue();
                        left = right = (int) pixelValues[1].getValue();
                    } else if (pixelValues.length == 4) {
                        top = (int) pixelValues[0].getValue();
                        bottom = (int) pixelValues[2].getValue();
                        left = (int) pixelValues[3].getValue();
                        right = (int) pixelValues[1].getValue();
                    }
                    if (top != -1 && bottom != -1 && left != -1 && right != -1) {
                        ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(left, top,
                                right, bottom);
                    }
                }
            }
            break;

            case ATTR_MARGIN_RIGHT:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = marginLayoutParams.leftMargin;
                    int right = Utils.toInt(value);
                    int top = marginLayoutParams.topMargin;
                    int bottom = marginLayoutParams.bottomMargin;

                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_MARGIN_LEFT:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = Utils.toInt(value);
                    int right = marginLayoutParams.rightMargin;
                    int top = marginLayoutParams.topMargin;
                    int bottom = marginLayoutParams.bottomMargin;
                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_MARGIN_TOP:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = marginLayoutParams.leftMargin;
                    int right = marginLayoutParams.rightMargin;
                    int top = Utils.toInt(value);
                    int bottom = marginLayoutParams.bottomMargin;

                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_MARGIN_BOTTOM:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = marginLayoutParams.leftMargin;
                    int right = marginLayoutParams.rightMargin;
                    int top = marginLayoutParams.topMargin;
                    int bottom = Utils.toInt(value);

                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_PADDING: {
                PixelValue[] pixelValues = Utils.pixelPairs(value.toString());
                int top = -1;
                int bottom = -1;
                int left = -1;
                int right = -1;
                if (pixelValues.length == 1) {
                    top = bottom = left = right = (int) pixelValues[0].getValue();
                } else if (pixelValues.length == 2) {
                    top = bottom = (int) pixelValues[0].getValue();
                    left = right = (int) pixelValues[1].getValue();
                } else if (pixelValues.length == 4) {
                    top = (int) pixelValues[0].getValue();
                    bottom = (int) pixelValues[2].getValue();
                    left = (int) pixelValues[3].getValue();
                    right = (int) pixelValues[1].getValue();
                }
                if (top != -1 && bottom != -1 && left != -1 && right != -1) {
                    v.setPadding(left, top, right, bottom);
                }
            }
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
                if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
                    ((AbsoluteLayout.LayoutParams) layoutParams).x = Utils.toInt(value);
                }
                break;

            case ATTR_TOP:
                if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
                    ((AbsoluteLayout.LayoutParams) layoutParams).y = Utils.toInt(value);
                }
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

                if (viewAttrHandler != null) {
                    viewAttrHandler.apply(context, tagName, v, params, value, innerElement);
                }

                // If there extra attr is set, then should be applied also.
                if (extraAttrHandler != null) {
                    extraAttrHandler.apply(context, tagName, v, params, value, innerElement);
                }

                // finally apply corresponding parent attr to child
                if (parentAttr != null) {
                    parentAttr.applyToChild(context, tagName, v, parent, params, value);
                }
                break;
        }

    }
}
