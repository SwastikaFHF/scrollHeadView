package com.aitangba.testproject.html;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XBeats on 2019/12/10
 */
public class AElement implements HtmlHelper.Element {

    private HashMap<String, String> mMap = new HashMap<>();
    private static final String MODE = "(.*?):(.*?);";

    private int startIndex = 0;
    private int endIndex = 0;

    @Override
    public String getOriginElement() {
        return "a";
    }

    @Override
    public void onCreate(Attributes attrs) {
        String style = attrs.getValue("style");

        if (!TextUtils.isEmpty(style)) {
            if (!style.endsWith(";")) {
                style = style + ";";
            }

            Pattern pattern = Pattern.compile(MODE, Pattern.CASE_INSENSITIVE);  //忽略大小写
            Matcher matcher = pattern.matcher(style);
            while (matcher.find()) {
                String key = matcher.group(1).trim();
                if ("color".equals(key)) {
                    mMap.put(key, matcher.group(2).trim());
                }
            }
        }

        String href = attrs.getValue("href");
        if (!TextUtils.isEmpty(href)) {
            mMap.put("href", href);
        }
    }

    @Override
    public void handleTag(boolean open, String tag, Editable output) {
        if (open) {
            startIndex = output.length();
        } else {
            endIndex = output.length();
            if (mMap.containsKey("color")) {
                output.setSpan(new ForegroundColorSpan(Color.parseColor(mMap.get("color"))), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (mMap.containsKey("href")) {
                output.setSpan(new CustomURLSpan(mMap.get("href")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mMap.clear();
        }
    }

    private static class CustomURLSpan extends URLSpan {

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
    }
}
