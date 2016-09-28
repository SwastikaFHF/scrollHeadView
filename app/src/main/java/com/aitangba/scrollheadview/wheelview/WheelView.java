package com.aitangba.scrollheadview.wheelview;

import android.content.Context;
import android.database.DataSetObserver;
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

    private int ITEM_HEIGHT_DEFAULT = 200;
    private int mItemHeight;
    private Scroller mScroller;
    private ListAdapter mAdapter;
    private DataSetObserver mDataSetObserver;

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

        final int childCount = 4;
        mOffset = 0;
        mDataOffset = 0;

        if(getChildCount() == 0 && mAdapter.getCount() > 0) {
            for(int i = 0; i< childCount; i++) {
                addView(mAdapter.getView(i, null, this));
            }
        } else {
            requestLayout();
        }
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

    private int mOffset;  //-count * itemHeight <--> count * itemHeight
    private int mDataOffset; //最大值 = 数据长度 * itemHeight

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

    private class AdapterDataSetObserver extends DataSetObserver {

        public void onChanged() {
            clearFocus();
            makeAndAddChild();
        }

        public void onInvalidated() {
            requestLayout();
        }
    }
}
