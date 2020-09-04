package com.aitangba.testproject.html;

import android.graphics.Color;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import org.xml.sax.Attributes;

import java.util.Map;
import java.util.Objects;

/**
 * Created by XBeats on 2019/12/10
 */
public class SpanElementHandler extends ElementHandler {

    @Override
    @NonNull
    public String getElement() {
        return "span";
    }

    @Override
    public void onCreate(Map<String, String> attrMap, Attributes attrs) {
        String style = attrs.getValue(ELEMENT_STYLE);
        String color = getStyleColor(style);
        if (color != null) {
            attrMap.put(ELEMENT_STYLE_COLOR, color);
        }

        String fontSize = getStyleFontSize(style);
        if (fontSize != null) {
            attrMap.put(ELEMENT_STYLE_FONT_SIZE, fontSize);
        }
    }

    @Override
    public void handleTag(int startIndex, Map<String, String> attrMap, Editable output) {
        final int endIndex = output.length();
        // 处理包含关系
        if (attrMap.containsKey(ELEMENT_STYLE_COLOR)) {
            final int color = Color.parseColor(attrMap.get(ELEMENT_STYLE_COLOR));
            ForegroundColorSpan[] foregroundColorSpans = output.getSpans(startIndex, endIndex, ForegroundColorSpan.class);
            if (foregroundColorSpans != null && foregroundColorSpans.length > 0) {
                int tempStart = startIndex;
                for (ForegroundColorSpan item : foregroundColorSpans) {
                    output.setSpan(new ForegroundColorSpan(color), tempStart, output.getSpanStart(item), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tempStart = output.getSpanEnd(item);
                }
                if(tempStart < endIndex) {
                    output.setSpan(new ForegroundColorSpan(color), tempStart, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                output.setSpan(new ForegroundColorSpan(Color.parseColor(attrMap.get(ELEMENT_STYLE_COLOR))), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (attrMap.containsKey(ELEMENT_STYLE_FONT_SIZE)) {
            String sizeStr = Objects.requireNonNull(attrMap.get(ELEMENT_STYLE_FONT_SIZE)).replaceAll("[A-Za-z]", "");
            int size = -1;
            try {
                size = Integer.parseInt(sizeStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (size > -1) {
                output.setSpan(new AbsoluteSizeSpan(size, true), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
