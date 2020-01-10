package com.aitangba.testproject.html;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;

import java.util.HashMap;

/**
 * Created by XBeats on 2019/12/10
 */
public class HtmlParser {

    public static Spanned fromHtml(String source) {
        return getParser().parse(source);
    }

    public static HtmlParser getParser() {
        return new HtmlParser()
                .registerElement(new SpanElementHandler())
                .registerElement(new AElementHandler());
    }

    private HashMap<String, ElementHandler> mElementHandlers = new HashMap<>();

    public HtmlParser registerElement(@NonNull ElementHandler elementHandler) {
        mElementHandlers.put(elementHandler.getElement(), elementHandler);
        return this;
    }

    public Spanned parse(String source) {
        return Html.fromHtml(source, null, new HtmlHelper.CustomHandler(mElementHandlers));
    }
}
