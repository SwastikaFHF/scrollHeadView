package com.aitangba.scrollheadview.wheelview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fhf11991 on 2016/3/28.
 */
public class WheelView extends ViewGroup {

    private static final String TAG = "WheelView";

    private int ITEM_HEIGHT_DEFAULT = 200;
    private int mItemHeight;
    private Scroller mScroller;
    private List<String> mList;

    public void setList(List<String> list) {
        mList = list;
    }

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    private GestureDetector mGestureDetector;

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new CustomGestureListener());
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        mItemHeight = ITEM_HEIGHT_DEFAULT;
        if(getChildCount() > 0) {
            View child = getChildAt(0);
            mItemHeight = child.getMeasuredHeight();
        }
        setMeasuredDimension(widthSize, mItemHeight * 3);
    }

    private int mOffset;
    private int mDataOffset;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int height = getMeasuredHeight();
        final int itemHeight = mItemHeight;
        final int count = getChildCount();
        final int totalHeight = count * itemHeight;
        int viewCycleCount = mDataOffset / (count - 1) * itemHeight;

        int tempViewTop = mOffset;
        for(int i = 0  ; i < getChildCount() ; i ++) {
            View child = getChildAt(i);
            if(tempViewTop >= height) { // 向下
                tempViewTop = tempViewTop - totalHeight;
                viewCycleCount = viewCycleCount - 1;
            } else if(tempViewTop <= -itemHeight) {  //向上
                tempViewTop = tempViewTop + totalHeight;
                viewCycleCount = viewCycleCount + 1;
            }
            child.layout(l, tempViewTop, r, tempViewTop + itemHeight);
            int position = viewCycleCount * (count - 1) + i % count;
            setViewData(child, position);

            tempViewTop += itemHeight;

        }
    }

    private void setViewData(View child, int position) {
        if(child instanceof TextView) {
            TextView textView = (TextView) child;
//            textView.setText(mList.get(position));
        }
    }

    private void doScroll(int delta) {
        final int itemHeight = mItemHeight;
        final int count = getChildCount();
        final int totalHeight = count * itemHeight;
        final int listTotalHeight = mList.size() * itemHeight;

        mOffset = (mOffset + delta) % totalHeight;
        mDataOffset = (mDataOffset + delta) % listTotalHeight;
        requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            return false;
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
    private static final int MESSAGE_FLING = 1;

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

    private boolean mIsCyclic = true;
    private int lastScrollY = 0;

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
}
