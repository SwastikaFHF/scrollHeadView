package com.aitangba.testproject.common.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2016/10/24.
 */
public class PageIndicatorView extends ViewGroup{

    private ImageView mFocusedView;

    private ViewPager mViewPager;
    private int mMargin;

    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        createFocusedView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true); //按序绘制

        final float scale = getResources().getDisplayMetrics().density;
        mMargin = (int)(10 * scale + 0.5F);
    }

    private void createFocusedView() {
        mFocusedView = new ImageView(getContext());
        mFocusedView.setScaleType(ImageView.ScaleType.CENTER);
        mFocusedView.setImageResource(R.drawable.ic_ad_dot_focused);

        addView(mFocusedView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);


        final int childCount = getChildCount();
        final int FOCUSED_VIEW_COUNT = 1;

        int width;
        if(widthModel == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int childWidth = childCount == 0 ? 0 : getChildAt(0).getMeasuredWidth();
            width = (childWidth + mMargin) * (childCount - FOCUSED_VIEW_COUNT);
        }

        int height;
        if(heightModel == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = childCount == 0 ? 0 : getChildAt(0).getMeasuredHeight();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i = 0 ; i < getChildCount() ; i++) {
            final View view = getChildAt(i);
            final int childWidth = view.getMeasuredWidth();

            if(view == mFocusedView) {
                if(mViewPager != null) {
                    int position = mViewPager.getCurrentItem();
                    int left = mMargin / 2 + position * childWidth;
                    view.layout(left, t, left + childWidth, b);
                }
            } else {
                int position = i - 1;
                int left = mMargin / 2 + position * (childWidth + mMargin);
                view.layout(left, t, left + childWidth, b);
            }
        }
    }

    public void initView(int size) {
        //remove all childView except focusedView
        for(int i = 0 ; i < getChildCount() ; i++) {
            final View view = getChildAt(i);
            if(view != mFocusedView) {
                removeView(view);
            }
        }

        if(size <= 1) {
            setVisibility(View.GONE);
            return;
        } else {
            setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < size; i++) {
            final ImageView dotView = new ImageView(getContext());
            dotView.setScaleType(ImageView.ScaleType.CENTER);
            dotView.setImageResource(R.drawable.ic_ad_dot_unfocused);
            addView(dotView);
        }
    }

    public void setupWithViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            return;
        }

        mViewPager = viewPager;
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mFocusedView.bringToFront();
                mFocusedView.offsetLeftAndRight(positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
