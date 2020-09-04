package com.aitangba.testproject.view.HeaderTabLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by fhf11991 on 2017/3/31.
 */

public class HeaderTabLayout extends LinearLayout {


    private final static int STROKE_WIDTH = 3;

    private String[] mHeadTitles;
    private ViewPager mViewPager;

    private Paint mLinePaint;

    private int mCurrentPosition;

    public HeaderTabLayout(Context context) {
        this(context, null);
    }

    public HeaderTabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);//设置非填充
        mLinePaint.setStrokeWidth(STROKE_WIDTH);//笔宽5像素
        mLinePaint.setColor(Color.WHITE);

        setTitles(new String[]{"你好", "真棒","真棒真棒真棒真棒真棒真棒"});

        setCurrentPosition(0);
    }

    public void setTitles(String[] headTitles) {
        mHeadTitles = headTitles;
        if(mHeadTitles == null || mHeadTitles.length == 0) {
            return;
        }

        removeAllViews();

        TextView textView;
        for(int i = 0; i < mHeadTitles.length; i ++) {
            textView = new TextView(getContext());
            textView.setText(mHeadTitles[i]);
            textView.setPadding(24, 24, 24, 24);
            textView.setSingleLine();
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(Gravity.CENTER);
            final int position = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mViewPager != null) {
                        mViewPager.setCurrentItem(position);
                    }
                    setCurrentPosition(position);
                }
            });
            addView(textView);
        }

    }

    public void bindViewPager(ViewPager viewPager) {

        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCurrentPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setCurrentPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;

        final int childCount = getChildCount();
        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
//            if(i == mCurrentPosition) {
//                if(i == 0) {
//                    child.setBackgroundResource(R.drawable.bg_left_tab_pressed);
//                } else if(i == childCount - 1){
//                    child.setBackgroundResource(R.drawable.bg_right_tab_pressed);
//                } else {
//                    child.setBackgroundResource(R.drawable.bg_middle_tab_pressed);
//                }
//            } else {
//                if(i == 0) {
//                    child.setBackgroundResource(R.drawable.bg_left_tab);
//                } else if(i == childCount - 1){
//                    child.setBackgroundResource(R.drawable.bg_right_tab);
//                } else {
//                    child.setBackgroundResource(R.drawable.bg_middle_tab);
//                }
//            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);

        int childCount = getChildCount();
        int maxWidth = 0;
        for (int i = 0 ; i < childCount ; i ++) {
            maxWidth = Math.max(maxWidth, getChildAt(i).getMeasuredWidth());
        }
        if(width < maxWidth * childCount) {
            maxWidth = width / childCount;
        }

        for (int i = 0 ; i < childCount ; i ++) {
            View child = getChildAt(i);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = maxWidth;
            child.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int childCount = getChildCount();
        if(childCount < 3) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            float drawX = i * getChildAt(i).getMeasuredWidth();
            if(i == 0 || i == mCurrentPosition || (i - 1) == mCurrentPosition) {
                continue;
            } else {
                canvas.drawLine(drawX, 20, drawX, getMeasuredHeight() - 20, mLinePaint);
            }
        }
    }
}
