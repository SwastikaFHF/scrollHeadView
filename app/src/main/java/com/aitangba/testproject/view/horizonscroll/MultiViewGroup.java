package com.aitangba.testproject.view.horizonscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by fhf11991 on 2016/6/22.
 */
public class MultiViewGroup extends ViewGroup {

    private VelocityTracker mVelocityTracker; // 用于判断甩动手势
    private static final int SNAP_VELOCITY = 600; // X轴速度基值，大于该值时进行切换
    private Scroller mScroller;// 滑动控制
    private int mCurScreen; // 当前页面为第几屏
    private int mDefaultScreen = 0;
    private float mLastMotionX;// 记住上次触摸屏的位置
    private int deltaX;

    private OnViewChangeListener mOnViewChangeListener;

    public MultiViewGroup(Context context) {
        this(context, null);
    }

    public MultiViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(getContext());
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        mCurScreen = mDefaultScreen;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {// 会更新Scroller中的当前x,y位置
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(mCurScreen * width, 0);// 移动到第一页位置
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int margeLeft = 0;
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != View.GONE) {
                int childWidth = view.getMeasuredWidth();
                // 将内部子孩子横排排列
                view.layout(margeLeft, 0, margeLeft + childWidth,
                        view.getMeasuredHeight());
                margeLeft += childWidth;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                obtainVelocityTracker(event);
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                deltaX = (int) (mLastMotionX - x);
                if (canMoveDis(deltaX)) {
                    obtainVelocityTracker(event);
                    mLastMotionX = x;
                    // 正向或者负向移动，屏幕跟随手指移动
                    scrollBy(deltaX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 当手指离开屏幕时，记录下mVelocityTracker的记录，并取得X轴滑动速度
                obtainVelocityTracker(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                // 当X轴滑动速度大于SNAP_VELOCITY
                // velocityX为正值说明手指向右滑动，为负值说明手指向左滑动
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
                    // Fling enough to move left
                    snapToScreen(mCurScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY
                        && mCurScreen < getChildCount() - 1) {
                    // Fling enough to move right
                    snapToScreen(mCurScreen + 1);
                } else {
                    snapToDestination();
                }
                releaseVelocityTracker();
                break;
        }
        // super.onTouchEvent(event);
        return true;// 这里一定要返回true,不然只接受down
    }

    /**
     * 边界检测
     *
     * @param deltaX
     * @return
     */
    private boolean canMoveDis(int deltaX) {
        int scrollX = getScrollX();
        // deltaX<0说明手指向右划
        if (deltaX < 0) {
            if (scrollX <= 0) {
                return false;
            } else if (deltaX + scrollX < 0) {
                scrollTo(0, 0);
                return false;
            }
        }
        // deltaX>0说明手指向左划
        int leftX = (getChildCount() - 1) * getWidth();
        if (deltaX > 0) {
            if (scrollX >= leftX) {
                return false;
            } else if (scrollX + deltaX > leftX) {
                scrollTo(leftX, 0);
                return false;
            }
        }
        return true;
    }

    /**
     * 使屏幕移动到第whichScreen+1屏
     *
     * @param whichScreen
     */
    public void snapToScreen(int whichScreen) {
        int scrollX = getScrollX();
        if (scrollX != (whichScreen * getWidth())) {
            int delta = whichScreen * getWidth() - scrollX;
            mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 2);
            mCurScreen = whichScreen;
            invalidate();
            if (mOnViewChangeListener != null) {
                mOnViewChangeListener.OnViewChange(mCurScreen);
            }
        }
    }

    /**
     * 当不需要滑动时，会调用该方法
     */
    private void snapToDestination() {
        int screenWidth = getWidth();
        int whichScreen = (getScrollX() + (screenWidth / 2)) / screenWidth;
        snapToScreen(whichScreen);
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void SetOnViewChangeListener(OnViewChangeListener listener) {
        mOnViewChangeListener = listener;
    }

    public interface OnViewChangeListener {
        public void OnViewChange(int page);
    }
}
