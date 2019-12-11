package com.aitangba.testproject.html;

import android.text.Spanned;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XBeats on 2019/12/10
 */
public class HtmlUtils {

    public static Spanned fromHtml(String source) {
        List<HtmlHelper.Element> elements = new LinkedList<>();
        elements.add(new SpanElement());
        elements.add(new AElement());
        return HtmlHelper.fromHtml(source, elements);
    }
}
