package com.aitangba.testproject.html;

import android.graphics.Color;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;

import org.xml.sax.Attributes;

import java.util.Map;

/**
 * Created by XBeats on 2019/12/10
 */
public class AElementHandler extends ElementHandler {

    private static final String ELEMENT_HREF = "href";
    private OnClickListener mClickListener;

    public AElementHandler() {
        this(null);
    }

    public AElementHandler(OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    @NonNull
    public String getElement() {
        return "a";
    }

    @Override
    public void onCreate(Map<String, String> attrMap, Attributes attrs) {
        String style = attrs.getValue(ELEMENT_STYLE);
        String styleColor = getStyleColor(style);
        if (styleColor != null) {
            attrMap.put(ELEMENT_STYLE_COLOR, styleColor);
        }

        String href = attrs.getValue(ELEMENT_HREF);
        if (!TextUtils.isEmpty(href)) {
            attrMap.put(ELEMENT_HREF, href);
        }
    }

    @Override
    public void handleTag(int startIndex, Map<String, String> attrMap, Editable output) {
        final int endIndex = output.length();
        if (attrMap.containsKey(ELEMENT_STYLE_COLOR)) {
            output.setSpan(new ForegroundColorSpan(Color.parseColor(attrMap.get(ELEMENT_STYLE_COLOR))), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (attrMap.containsKey(ELEMENT_HREF)) {
            output.setSpan(new CustomURLSpan(attrMap.get(ELEMENT_HREF)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private class CustomURLSpan extends URLSpan {

        CustomURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            int originColor = ds.getColor();
            super.updateDrawState(ds);
            ds.setColor(originColor);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            if (mClickListener != null) {
                mClickListener.onClick(widget, getURL());
                return;
            }
            super.onClick(widget);
        }
    }

    public interface OnClickListener {
        void onClick(View widget, String url);
    }
}
