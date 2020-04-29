package com.aitangba.testproject.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;

public class TextDrawableView extends View {

    private static float TEXT_LIST = 14f;
    private Paint mPaint;
    private Bitmap mRightBitmap;
    private String mText = "创业项目调研：［创业CEO与草根CTO社交约见平台］， 各位技术牛人，如果有A轮或B轮以上的创业公司邀请您做技术合伙人或CTO，会考虑出来共同创业吗？";
    private float mTextSize;
    private int mWidthPixels;
    private int mMaxWidth;
    private OnDrawableClick mOnDrawableClick;

    public TextDrawableView(Context context) {
        super(context);
        initView(context);
    }

    public TextDrawableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextDrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        mTextSize = (int) (TEXT_LIST * scale + 0.5f);

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(mTextSize);

        mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        mRightBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获取宽-测量规则的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高-测量规则的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        // 类似TextView,ImageView等针对wrap_content均在onMeasure()对设置默认宽 / 高值有特殊处理,具体读者可以自行查看
        @SuppressLint("DrawAllocation") Rect rect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), rect);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        int mWidth = Math.min(rect.width() + mRightBitmap.getWidth(), mWidthPixels);
        int mHeight = Math.max(rect.height() + (int) fontMetrics.bottom, mRightBitmap.getHeight());

        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }


    private static final String PLACE = "...";
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), rect);
        int textLength = rect.width();
        float textTop = Math.max((mRightBitmap.getHeight() - rect.height()) / 2, 0) + mTextSize;
        float drawableTop = mRightBitmap.getHeight() - rect.height() > 0 ? 0 : Math.max((rect.height() - mRightBitmap.getHeight()), 0);
        mMaxWidth = Math.min(rect.width() + mRightBitmap.getWidth(), mWidthPixels);
        if (mMaxWidth - textLength < mRightBitmap.getWidth()) {
            Rect textShowRect = new Rect();
            mPaint.getTextBounds(PLACE, 0, PLACE.length(), textShowRect);
            final int spaceLeft = mMaxWidth - mRightBitmap.getWidth() - textShowRect.width() - 10;
            textShowRect.setEmpty();

            StringBuilder textShow = new StringBuilder();
            char[] textChars = mText.toCharArray();
            for (char textChar : textChars) {
                String textShowTemp = textShow.toString() + textChar;//aaa是占位字符串
                mPaint.getTextBounds(textShowTemp, 0, textShowTemp.length(), textShowRect);
                if (textShowRect.width() > spaceLeft) {
                    break;
                }
                textShow.append(textChar);
            }
            textShow.append(PLACE);
            canvas.drawText(textShow.toString(), 10, textTop, mPaint);
            canvas.drawBitmap(mRightBitmap, mMaxWidth - mRightBitmap.getWidth(), drawableTop, null);
        } else {
            canvas.drawText(mText, 0, textTop, mPaint);
            canvas.drawBitmap(mRightBitmap, textLength, drawableTop, null);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        boolean drawableIsClick = x > mMaxWidth - mRightBitmap.getWidth();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (drawableIsClick && mOnDrawableClick != null) {
                mOnDrawableClick.onClick();
            }
        }
        return drawableIsClick;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setRightDrawable(@DrawableRes int drawable) {
        if (drawable != 0) {
            mRightBitmap = BitmapFactory.decodeResource(getResources(), drawable);
        } else {
            mRightBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        }
    }

    public void setTextSize(Context context, float spValue) {
        if (spValue < 0) return;
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        mTextSize = (int) (spValue * scale + 0.5f);
        mPaint.setTextSize(mTextSize);
    }

    public void setTextColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    public void setOnDrawableClick(OnDrawableClick onDrawableClick) {
        mOnDrawableClick = onDrawableClick;
    }

    interface OnDrawableClick {
        void onClick();
    }

}
