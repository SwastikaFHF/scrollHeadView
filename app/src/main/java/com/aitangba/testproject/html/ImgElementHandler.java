package com.aitangba.testproject.html;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import org.xml.sax.Attributes;

import java.util.Map;

/**
 * Created by XBeats on 2020/1/9
 */
public class ImgElementHandler extends ElementHandler {

    private Context mApplication;
    private final int mDrawableId;
    private OnClickListener mOnClickListener;

    public ImgElementHandler(Context application, @DrawableRes int drawableId) {
        this(application, drawableId, null);
    }

    public ImgElementHandler(Context application, @DrawableRes int drawableId, OnClickListener onClickListener) {
        mApplication = application.getApplicationContext();
        mDrawableId = drawableId;
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    String getElement() {
        return "img";
    }

    @Override
    void onCreate(Map<String, String> attrMap, Attributes attrs) {
        String src = attrs.getValue("src");
        if (src != null) {
            attrMap.put("src", src);
        }
    }

    @Override
    void handleTag(int startIndex, Map<String, String> attrMap, Editable output) {
        startIndex = output.length();
        output.append("\uFFFC");
        output.setSpan(new CenterAlignImageSpan(mApplication, mDrawableId), startIndex, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        output.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(widget, getElement());
                }
            }
        }, startIndex, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static class CenterAlignImageSpan extends ImageSpan {
        private CenterAlignImageSpan(Context context, int drawableId) {
            super(context, drawableId);
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();

            if (fm != null) {
                final int textMiddleY = (fm.bottom + fm.top) / 2; // 获取无图片状态时，文本框中线y值
                fm.ascent = textMiddleY - (rect.bottom - rect.top) / 2; // 加入图片后，文本框的宽高相应变化，保证文本框y值居中
                fm.descent = textMiddleY + (rect.bottom - rect.top) / 2;

                fm.top = fm.ascent;
                fm.bottom = fm.descent;
            }
            return rect.right;
        }
    }

    public interface OnClickListener {
        void onClick(@NonNull View widget, String tag);
    }
}
