package com.aitangba.testproject.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;

import org.xml.sax.Attributes;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XBeats on 2020/1/8
 */
public abstract class ElementHandler {

    static final String ELEMENT_STYLE = "style";
    static final String ELEMENT_STYLE_COLOR = "color";
    static final String ELEMENT_STYLE_FONT_SIZE = "font_size";

    private static Pattern sForegroundColorPattern;
    private static Pattern sFontSizePattern;

    private static Pattern getForegroundColorPattern() {
        if (sForegroundColorPattern == null) {
            sForegroundColorPattern = Pattern.compile(
                    "(.*?)color:(.*?)(;|$)");
        }
        return sForegroundColorPattern;
    }

    private static Pattern getFontSizePattern() {
        if (sFontSizePattern == null) {
            sFontSizePattern = Pattern.compile(
                    "(.*?)font-size:(.*?)(;|$)");
        }
        return sFontSizePattern;
    }

    @Nullable
    final String getStyleColor(@Nullable String style) {
        if (style == null) {
            return null;
        }
        Matcher m = getForegroundColorPattern().matcher(style);
        if (m.find()) {
            return m.group(2);
        }
        return null;
    }

    @Nullable
    final String getStyleFontSize(@Nullable String style) {
        if (style == null) {
            return null;
        }
        Matcher m = getFontSizePattern().matcher(style);
        if (m.find()) {
            return m.group(2);
        }
        return null;
    }

    @NonNull
    abstract String getElement();

    abstract void onCreate(Map<String, String> attrMap, Attributes attrs);

    abstract void handleTag(int startIndex, Map<String, String> attrMap, Editable output);
}
