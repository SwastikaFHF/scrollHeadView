package com.aitangba.testproject.html;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XBeats on 2019/12/10
 */
public class SpanElement implements HtmlHelper.Element {

    private static final String MODE = "(.*?):(.*?);";
    private HashMap<String, String> mMap = new HashMap<>();
    private int startIndex = 0;
    private int endIndex = 0;

    @Override
    public String getOriginElement() {
        return "span";
    }

    @Override
    public void onCreate(Attributes attrs) {
        String style = attrs.getValue("style");
        if (!style.endsWith(";")) {
            style = style + ";";
        }

        Pattern pattern = Pattern.compile(MODE, Pattern.CASE_INSENSITIVE);  //忽略大小写
        Matcher matcher = pattern.matcher(style);
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            if ("color".equals(key)) {
                mMap.put(key, matcher.group(2).trim());
            } else if ("font-size".equals(key)) {
                mMap.put(key, matcher.group(2).trim());
            }
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

            if (mMap.containsKey("font-size")) {
                String sizeStr = mMap.get("font-size").replaceAll("[A-Za-z]", "");
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

            mMap.clear();
        }
    }
}
