package com.mozz.htmlnative.attrs;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.mozz.htmlnative.AttrApplyException;
import com.mozz.htmlnative.HNRenderer;
import com.mozz.htmlnative.HtmlTag;
import com.mozz.htmlnative.common.Utils;

public class TextViewAttr extends Attr {

    private static final String FONT_SIZE = "font-size";
    private static final String COLOR = "color";
    private static final String TEXT = "text";
    private static final String LINE_HEIGHT = "line-height";
    private static final String FONT_STYLE = "font-style";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String FONT_ALIGN = "text-align";
    private static final String HREF = "href";
    private static final String TEXT_WORD_SPACING = "word-spacing";
    private static final String TEXT_OVER_FLOW = "text-overflow";

    @NonNull
    private static TextViewAttr sInstance = new TextViewAttr();

    @NonNull
    public static TextViewAttr getInstance() {
        return sInstance;
    }

    @Override
    public void apply(final Context context, @NonNull java.lang.String tag, View v, @NonNull java
            .lang.String params, @NonNull final Object value, @NonNull String innerElement)
            throws AttrApplyException {
        final TextView textView = (TextView) v;

        switch (params) {
            case COLOR:
                textView.setTextColor(Utils.color(value));
                break;

            case TEXT:
                textView.setText(value.toString());
                break;

            case FONT_SIZE:
                PixelValue size = Utils.toPixel(value);
                if (size.getUnit() == PixelValue.UNSET) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) size.getValue());
                } else {
                    textView.setTextSize(size.getUnit(), (float) size.getValue());
                }
                break;

            case LINE_HEIGHT:
                float lineHeight = 1.f;
                if (value instanceof Integer) {
                    lineHeight = (float) (int) value;
                } else if (value instanceof Float) {
                    lineHeight = (float) value;
                }
                textView.setLineSpacing(lineHeight, 0);
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
                }

                break;

            case HREF:
                if (tag.equals(HtmlTag.A)) {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (HNRenderer.getHrefLinkHandler() != null) {
                                HNRenderer.getHrefLinkHandler().onHref(value.toString(), textView);
                            }
                        }
                    });
                }
                break;

            case FONT_ALIGN:
                java.lang.String val = value.toString();
                switch (val) {
                    case "center":
                        textView.setGravity(Gravity.CENTER);
                        break;
                    case "left":
                        textView.setGravity(Gravity.LEFT);
                        break;
                    case "right":
                        textView.setGravity(Gravity.RIGHT);
                        break;
                }

                break;

            case TEXT_WORD_SPACING: {
                String ss = value.toString();
                if (ss.equals("normal")) {

                } else {
                    Float f = Utils.toFloat(value);
                    textView.setLetterSpacing(f);
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
        }
    }

    @Override
    public void setDefault(Context context, String tag, View v, String innerElement) throws
            AttrApplyException {

        TextView textView = (TextView) v;

        if (!TextUtils.isEmpty(innerElement) && TextUtils.isEmpty(textView.getText())) {
            textView.setText(innerElement);
        }
    }
}
