package com.aitangba.testproject.wheelview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * Created by fhf11991 on 2016/3/28.
 */
public class WheelView extends ViewGroup {

    private static final String TAG = "WheelView";

    private static final int MESSAGE_FLING = 1;
    private static final int CACHE_VIEW_SIZE = 1; //缓存展示的view个数

    private Scroller mScroller;
    private ListAdapter mAdapter;
    private DataSetObserver mDataSetObserver;
    private GestureDetector mGestureDetector;

    private int mOffset;  //-count * itemHeight <--> count * itemHeight
    private int mItemHeight;
    private int mDataOffset; //最大值 = 数据长度 * itemHeight
    private boolean mIsCyclic = true;
    private int lastScrollY = 0;
    private int mDisplayViewsCount = 3;

    public WheelView(Context context) {
        this(context, null);
    }
    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new CustomGestureListener());
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int height;
        if(heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            if(getChildCount() > 0) {
                View child = getChildAt(0);
                mItemHeight = child.getMeasuredHeight();
            }

            final int heightNeed = mItemHeight * mDisplayViewsCount;
            if(heightMode == MeasureSpec.UNSPECIFIED) {
                height = heightNeed;
            } else {   //MeasureSpec.AT_MOST
                height = Math.min(heightNeed, heightSize);
            }
        }
        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int height = getMeasuredHeight();
        final int itemHeight = mItemHeight;
        final int count = getChildCount();
        final int totalHeight = count * itemHeight;

        int tempViewTop = mOffset;
        for(int i = 0  ; i < count ; i ++) {
            View child = getChildAt(i);
            if(tempViewTop >= height) {             // 向下
                tempViewTop = tempViewTop - totalHeight;
            } else if(tempViewTop <= -itemHeight) {  //向上
                tempViewTop = tempViewTop + totalHeight;
            }
            child.layout(l, tempViewTop, r, tempViewTop + itemHeight);
            tempViewTop += itemHeight;
        }

        final int size = mAdapter == null ? 0 : mAdapter.getCount();
        final int dataTotalHeight = size * itemHeight;
        final int dataStartIndex = Math.abs((mDataOffset <= 0 ? mDataOffset : dataTotalHeight - mDataOffset)/ itemHeight);
        final int viewStartIndex = Math.abs((mOffset <= 0 ? mOffset : totalHeight - mOffset)/ itemHeight);

        int dataIndex = dataStartIndex;
        int viewIndex = viewStartIndex;
        for(int i = 0  ; i < count ; i ++) {
            View child = getChildAt(viewIndex);
            setViewData(child, dataIndex);

            dataIndex = (dataIndex + 1) % size;
            viewIndex = (viewIndex + 1) % count;
        }
    }

    private Drawable mTopDrawable;
    private Drawable mBottomDrawable;
    private final static int mSelectionDividerHeight = 2; //选择线的宽度
    private boolean mShowSelectorWheel = true;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(mTopDrawable == null){
            int colors[] = { 0xffffffff , 0x3fffffff, 0x00ffffff };//开始颜色，中间颜色，结束颜色
            int bottom =  mItemHeight - mSelectionDividerHeight / 2;
            mTopDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
            mTopDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
        }
        mTopDrawable.draw(canvas);

        if(mBottomDrawable == null){
            int colors[] = { 0xffffffff , 0x3fffffff, 0x00ffffff };//开始颜色，中间颜色，结束颜色
            mBottomDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
            int top = (mDisplayViewsCount - 1) * mItemHeight + mSelectionDividerHeight / 2;
            int bottom = mDisplayViewsCount * mItemHeight;
            mBottomDrawable.setBounds(0, top, getMeasuredWidth(), bottom);
        }
        mBottomDrawable.draw(canvas);

        if(mShowSelectorWheel) {
            final int middlePosition = (int) Math.ceil((double) mDisplayViewsCount / 2);
            final int upLintY = (middlePosition - 1) * mItemHeight;
            final int underLintY = middlePosition * mItemHeight;
            final int right = getMeasuredWidth();

            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#d5dce4"));
            paint.setStrokeWidth(mSelectionDividerHeight);
            canvas.drawLine(0, upLintY, right, upLintY, paint);
            canvas.drawLine(0, underLintY, right, underLintY, paint);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;
        if(mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
            makeAndAddChild();
        }
    }

    private void makeAndAddChild() {
        if(mAdapter == null)return;

        invalidateLayouts();

        if(getChildCount() == 0 && mAdapter.getCount() > 0) {
            final int count = mAdapter.getCount();
            final boolean isNeedCyclic = count > mDisplayViewsCount;
            final int childNeed = isNeedCyclic ? mDisplayViewsCount + CACHE_VIEW_SIZE : count;
            mDisplayViewsCount = isNeedCyclic ? mDisplayViewsCount : count;
            mIsCyclic = mIsCyclic && isNeedCyclic;
            for(int i = 0; i< childNeed; i++) {
                addView(mAdapter.getView(i, null, this));
            }
        } else {
            requestLayout();
        }
    }

    private void invalidateLayouts() {
        mOffset = 0;
        mDataOffset = 0;
    }

    private void setViewData(View child, int position) {
        if(mAdapter != null) {
            mAdapter.getView(position, child, this);
        }
    }

    private void doScroll(int delta) {
        final int itemHeight = mItemHeight;
        final int count = getChildCount();
        final int totalHeight = count * itemHeight;
        final int listTotalHeight = (mAdapter == null ? 0 : mAdapter.getCount()) * itemHeight;

        mOffset = (mOffset + delta) % totalHeight;
        mDataOffset = (mDataOffset + delta) % listTotalHeight;
        requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if(!mIsCyclic || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            mScroller.forceFinished(true);
            return super.dispatchTouchEvent(ev);
        }
        return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        doFling();
    }

    private void doFling() {
        animationHandler.sendEmptyMessage(MESSAGE_FLING);
    }


    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FLING:
                    if(!mScroller.computeScrollOffset()) {
                        return;
                    }
                    int currY = mScroller.getCurrY();
                    int delta = lastScrollY - currY;
                    lastScrollY = currY;
                    if (delta != 0) {
                        doScroll(delta);
                    }
                    break;
                default:break;
            }
        }
    };

    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            doScroll((int) -distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int maxY = mIsCyclic ? 0x7FFFFFFF : getChildCount() * mItemHeight;
            int minY = mIsCyclic ? -maxY : 0;
            lastScrollY = 0;
            mScroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY, maxY);
            WheelView.this.doFling();
            return true;
        }
    }

    private class AdapterDataSetObserver extends DataSetObserver {

        public void onChanged() {
            mScroller.forceFinished(true);
            clearFocus();
            makeAndAddChild();
        }

        public void onInvalidated() {
            requestLayout();
        }
    }
}
