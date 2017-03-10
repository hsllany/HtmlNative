package com.mozz.remoteview.attrs;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mozz.remoteview.AttrApplyException;
import com.mozz.remoteview.HtmlTag;
import com.mozz.remoteview.RVDomTree;
import com.mozz.remoteview.RVRenderer;
import com.mozz.remoteview.common.Utils;

public class TextViewAttr implements Attr {

    public static final String FONT_SIZE = "font-size";
    public static final String COLOR = "color";
    public static final String TEXT = "text";
    public static final String LINE_HEIGHT = "line-height";
    public static final String FONT_STYLE = "font-style";
    public static final String FONT_WEIGHT = "font-weight";
    public static final String HREF = "href";

    @NonNull
    private static TextViewAttr sInstance = new TextViewAttr();

    @NonNull
    public static TextViewAttr getInstance() {
        return sInstance;
    }

    @Override
    public void apply(final Context context, @NonNull String tag, View v, @NonNull String params, @NonNull final Object value, @NonNull RVDomTree tree)
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
                float size = Utils.px(value);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
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
                String s = value.toString();

                if (s.equals("bold")) {
                    textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                } else if (s.equals("normal")) {
                    textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                }

                break;

            case FONT_STYLE:
                String s2 = value.toString();

                if (s2.equals("italic")) {
                    int style = textView.getTypeface().getStyle();

                    if (style == Typeface.BOLD)
                        style = Typeface.BOLD_ITALIC;
                    else
                        style = Typeface.ITALIC;

                    textView.setTypeface(Typeface.DEFAULT, style);
                }

                break;

            case HREF:
                if (tag.equals(HtmlTag.A)) {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (RVRenderer.getHrefLinkHandler() != null)
                                RVRenderer.getHrefLinkHandler().onHref(value.toString(), textView);
                        }
                    });
                }
                break;
        }

        if (!TextUtils.isEmpty(tree.getInner()) && TextUtils.isEmpty(textView.getText())) {
            Log.d("TextViewAttr", tree.getInner());
            textView.setText(tree.getInner());
        }
    }
}
