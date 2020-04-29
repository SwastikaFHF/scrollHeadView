package com.aitangba.testproject.view.drawabletext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;

/**
 * Created by XBeats on 2020/2/24
 */
public class DrawableTextView extends AppCompatTextView {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    public DrawableTextView(Context context) {
        super(context);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        Log.d("DrawableTextView", "setCompoundDrawables");
        super.setCompoundDrawables(left == null ? null : new InnerDrawable(left, LEFT),
                top,
                right == null ? null : new InnerDrawable(right, RIGHT),
                bottom);
    }

    @NonNull
    @Override
    public Drawable[] getCompoundDrawables() {
        Drawable[] drawables = super.getCompoundDrawables();

        Drawable[] originDrawables = new Drawable[drawables.length];
        int i = 0;
        for (Drawable item : drawables) {
            if (item instanceof InnerDrawable) {
                InnerDrawable insetDrawable = (InnerDrawable) item;
                originDrawables[i] = insetDrawable.mDrawable;
            } else {
                originDrawables[i] = item;
            }
        }
        return originDrawables;
    }

    private class InnerDrawable extends Drawable {
        private Drawable mDrawable;
        private int mType;

        private InnerDrawable(@NonNull Drawable drawable, int type) {
            mDrawable = drawable;
            mType = type;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            float textWidth = getPaint().measureText(getText().toString().isEmpty() ? getHint().toString() : getText().toString());
            if (getGravity() == Gravity.CENTER || getGravity() == Gravity.CENTER_HORIZONTAL) {
                if (mType == LEFT) {
                    canvas.translate(getMeasuredWidth() / 2.0f - textWidth / 2 - getIntrinsicWidth() - getCompoundDrawablePadding(),
                            -mDrawable.getIntrinsicHeight() / 2.0f);
                } else if (mType == RIGHT) {
                    canvas.translate(-getMeasuredWidth() / 2.0f + textWidth / 2 + getCompoundDrawablePadding(),
                            -mDrawable.getIntrinsicHeight() / 2.0f);
                }
            }
            mDrawable.draw(canvas);
        }

        @Override
        public void setAlpha(int alpha) {
            mDrawable.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mDrawable.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return mDrawable.getOpacity();
        }

        @Override
        public int getIntrinsicHeight() {
            return mDrawable.getIntrinsicHeight();
        }

        @Override
        public int getIntrinsicWidth() {
            return mDrawable.getIntrinsicWidth();
        }
    }
}
