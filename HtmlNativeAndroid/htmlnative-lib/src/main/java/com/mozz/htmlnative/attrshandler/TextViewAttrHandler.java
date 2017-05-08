package com.mozz.htmlnative.attrshandler;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozz.htmlnative.HNativeEngine;
import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.HtmlTag;
import com.mozz.htmlnative.common.PixelValue;
import com.mozz.htmlnative.utils.ParametersUtils;
import com.mozz.htmlnative.css.InheritStylesRegistry;
import com.mozz.htmlnative.exception.AttrApplyException;

class TextViewAttrHandler extends AttrHandler {

    private static final String FONT_SIZE = "font-size";
    private static final String COLOR = "color";
    private static final String TEXT = "text";
    private static final String LINE_HEIGHT = "line-height";
    private static final String FONT_STYLE = "font-style";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String TEXT_ALIGN = "text-align";
    private static final String HREF = "href";
    private static final String TEXT_WORD_SPACING = "word-spacing";
    private static final String TEXT_OVER_FLOW = "text-overflow";
    private static final String TEXT_TRANSFORM = "text-transform";

    private static final int DEFAULT_SIZE = 14;
    private static final int DEFAULT_H1_SIZE = ParametersUtils.emToPx(2);
    private static final int DEFAULT_H1_PADDING = ParametersUtils.emToPx(0.67f);

    private static final int DEFAULT_H2_SIZE = ParametersUtils.emToPx(1.5f);
    private static final int DEFAULT_H2_PADDING = (int) ParametersUtils.emToPx(.75f);

    private static final int DEFAULT_H3_SIZE = (int) ParametersUtils.emToPx(1.17f);
    private static final int DEFAULT_H3_PADDING = (int) ParametersUtils.emToPx(.83f);

    private static final int DEFAULT_H4_SIZE = (int) ParametersUtils.emToPx(1.f);
    private static final int DEFAULT_H4_PADDING = (int) ParametersUtils.emToPx(1.12f);

    private static final int DEFAULT_H5_SIZE = (int) ParametersUtils.emToPx(0.8f);
    private static final int DEFAULT_H5_PADDING = (int) ParametersUtils.emToPx(1.12f);

    private static final int DEFAULT_H6_SIZE = (int) ParametersUtils.emToPx(.6f);
    private static final int DEFAULT_H6_PADDING = (int) ParametersUtils.emToPx(1.12f);

    private static final int DEFAULT_P_PADDING = (int) ParametersUtils.dpToPx(5);

    static {
        InheritStylesRegistry.register(FONT_SIZE);
        InheritStylesRegistry.register(COLOR);
        InheritStylesRegistry.register(LINE_HEIGHT);
        InheritStylesRegistry.register(FONT_STYLE);
        InheritStylesRegistry.register(FONT_WEIGHT);
        InheritStylesRegistry.register(TEXT_ALIGN);
        InheritStylesRegistry.register(TEXT_WORD_SPACING);
    }

    @Override
    public void apply(Context context, View v, DomElement domElement, View parent, ViewGroup
            .LayoutParams layoutParams, String params, final Object value, boolean isParent)
            throws AttrApplyException {

        final TextView textView = (TextView) v;
        switch (params) {
            case COLOR:
                textView.setTextColor(ParametersUtils.toColor(value));
                break;

            case TEXT:
                textView.setText(value.toString());
                break;

            case FONT_SIZE:
                PixelValue size = ParametersUtils.toPixel(value);
                if (size.getUnit() == PixelValue.UNSET) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) size.getValue());
                } else {
                    textView.setTextSize(size.getUnit(), (float) size.getValue());
                }
                break;

            case LINE_HEIGHT:
                float lineHeight = ParametersUtils.toFloat(value);
                textView.setLineSpacing(0, lineHeight);
                break;

            case FONT_WEIGHT:
                java.lang.String s = value.toString();

                if (s.equals("bold")) {
                    textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                } else if (s.equals("normal")) {
                    textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                }

                break;

            case FONT_STYLE:
                java.lang.String s2 = value.toString();

                if (s2.equals("italic")) {
                    int style = textView.getTypeface().getStyle();

                    if (style == Typeface.BOLD) {
                        style = Typeface.BOLD_ITALIC;
                    } else {
                        style = Typeface.ITALIC;
                    }

                    textView.setTypeface(Typeface.DEFAULT, style);
                } else if (s2.equals("normal")) {
                    int style = textView.getTypeface().getStyle();

                    if (style == Typeface.BOLD_ITALIC) {
                        style = Typeface.BOLD;
                    } else {
                        style = Typeface.NORMAL;
                    }

                    textView.setTypeface(Typeface.DEFAULT, style);
                }

                break;

            case HREF:
                if (domElement.getType().equals(HtmlTag.A)) {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (HNativeEngine.getHrefLinkHandler() != null) {
                                HNativeEngine.getHrefLinkHandler().onHref(value.toString
                                        (), textView);
                            }
                        }
                    });
                }
                break;

            case TEXT_ALIGN:
                java.lang.String val = value.toString();
                switch (val) {
                    case "center":
                        textView.setGravity(Gravity.CENTER);
                        break;
                    case "left":
                        textView.setGravity(Gravity.START);
                        break;
                    case "right":
                        textView.setGravity(Gravity.END);
                        break;
                }

                break;

            case TEXT_WORD_SPACING: {
                String ss = value.toString();
                if (ss.equals("normal")) {
                    textView.setLetterSpacing(textView.getLetterSpacing());
                } else {
                    PixelValue f = ParametersUtils.toPixel(value);
                    textView.setLetterSpacing((float) f.getEmValue());
                }
                break;
            }

            case TEXT_OVER_FLOW: {
                String ss = value.toString();

                if (ss.equals("ellipsis")) {
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                }
                break;
            }

            case TEXT_TRANSFORM: {
                switch (value.toString()) {
                    case "uppercase":
                        textView.setAllCaps(true);
                        break;
                    case "lowercase":
                        textView.setAllCaps(false);
                        break;
                }
            }
            break;
        }

    }

    @Override
    public void setDefault(Context context, View v, DomElement domElement, ViewGroup.LayoutParams
            layoutParams, View parent) throws AttrApplyException {

        TextView textView = (TextView) v;

        if (!TextUtils.isEmpty(domElement.getInner()) && TextUtils.isEmpty(textView.getText())) {
            textView.setText(domElement.getInner());
        }

        textView.setTextSize(DEFAULT_SIZE);

        switch (domElement.getType()) {
            case HtmlTag.H1:
                textView.setTextSize(DEFAULT_H1_SIZE);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                AttrsHelper.setPadding(textView, DEFAULT_H1_PADDING, textView.getPaddingLeft(),
                        DEFAULT_H1_PADDING, textView.getPaddingRight());
                AttrsHelper.setBold(textView);
                break;

            case HtmlTag.H2:
                textView.setTextSize(DEFAULT_H2_SIZE);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                AttrsHelper.setPadding(textView, DEFAULT_H2_PADDING, textView.getPaddingLeft(),
                        DEFAULT_H2_PADDING, textView.getPaddingRight());
                AttrsHelper.setBold(textView);
                break;

            case HtmlTag.H3:
                textView.setTextSize(DEFAULT_H3_SIZE);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                AttrsHelper.setPadding(textView, DEFAULT_H3_PADDING, textView.getPaddingLeft(),
                        DEFAULT_H3_PADDING, textView.getPaddingRight());
                AttrsHelper.setBold(textView);
                break;

            case HtmlTag.H4:
                textView.setTextSize(DEFAULT_H4_SIZE);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                AttrsHelper.setPadding(textView, DEFAULT_H4_PADDING, textView.getPaddingLeft(),
                        DEFAULT_H4_PADDING, textView.getPaddingRight());
                AttrsHelper.setBold(textView);
                break;

            case HtmlTag.H5:
                textView.setTextSize(DEFAULT_H5_SIZE);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                AttrsHelper.setPadding(textView, DEFAULT_H5_PADDING, textView.getPaddingLeft(),
                        DEFAULT_H5_PADDING, textView.getPaddingRight());
                AttrsHelper.setBold(textView);
                break;

            case HtmlTag.H6:
                textView.setTextSize(DEFAULT_H6_SIZE);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                AttrsHelper.setPadding(textView, DEFAULT_H6_PADDING, textView.getPaddingLeft(),
                        DEFAULT_H6_PADDING, textView.getPaddingRight());
                AttrsHelper.setBold(textView);
                break;

            case HtmlTag.P:
                AttrsHelper.setTopPadding(textView, DEFAULT_P_PADDING);
                AttrsHelper.setBottomPadding(textView, DEFAULT_P_PADDING);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                break;
            case HtmlTag.A:
                AttrsHelper.setUnderLine(textView);
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
        }
    }
}
