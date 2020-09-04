package com.aitangba.testproject.view.customswipe;

import android.content.Context;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

/**
 * Created by fhf11991 on 2016/6/22.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    private static final String TAG = "CustomSwipeRefreshLayout";

    private View[] mSwipeableChildren;
    private View mTarget; // the target of the gesture
    private View mCircleView;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof ImageView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    private void ensureImageView() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mCircleView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof ImageView) {
                    mCircleView = child;
                    break;
                }
            }
        }
    }

    /**
     * Set the children which can trigger a refresh by swiping down when they are visible. These
     * views need to be a descendant of this view.
     */
    public void setSwipeableChildren(final int... ids) {
        assert ids != null;

        // Iterate through the ids and find the Views
        mSwipeableChildren = new View[ids.length];
        for (int i = 0; i < ids.length; i++) {
            mSwipeableChildren[i] = findViewById(ids[i]);
        }
    }

    /**
     * This method controls when the swipe-to-refresh gesture is triggered. By returning false here
     * we are signifying that the view is in a state where a refresh gesture can start.
     *
     * <p>As {@link SwipeRefreshLayout} only supports one direct child by
     * default, we need to manually iterate through our swipeable children to see if any are in a
     * state to trigger the gesture. If so we return false to start the gesture.
     */
    @Override
    public boolean canChildScrollUp() {
        if (mSwipeableChildren != null && mSwipeableChildren.length > 0) {
            // Iterate through the scrollable children and check if any of them can not scroll up
            for (View view : mSwipeableChildren) {
                if (view != null && view.isShown() && !canViewScrollUp(view)) {
                    // If the view is shown, and can not scroll upwards, return false and start the
                    // gesture.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Utility method to check whether a {@link View} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
    private static boolean canViewScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(view, -1);
        } else {
            if (view instanceof AbsListView) {
                // Pre-ICS we need to manually check the first visible item and the child view's top
                // value
                final AbsListView listView = (AbsListView) view;
                return listView.getChildCount() > 0 &&
                        (listView.getFirstVisiblePosition() > 0
                                || listView.getChildAt(0).getTop() < listView.getPaddingTop());
            } else {
                // For all other view types we just check the getScrollY() value
                return view.getScrollY() > 0;
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mHeadView !=  null){
            headerHeight = mHeadView.getMeasuredHeight();
        }


        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget != null) {
            mTarget.measure(MeasureSpec.makeMeasureSpec(
                    getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight() + offsetY - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(getChildCount() <= 1)return;

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }

        final View target = mTarget;

        int heightTemp = -offsetY;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() + heightTemp;
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        target.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight + offsetY);
    }

    private int offsetY = 0;
    private int headerHeight= 0;
    private View mHeadView;

    public void setHeadViewId(int headViewId) {
        setHeadViewId(findViewById(headViewId));
    }

    public void setHeadViewId(View headView) {
        mHeadView = headView;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.d(TAG, "onNestedPreScroll");
        final int offsetYTemp = offsetY + dy;
        final int headerHeight = this.headerHeight;
        boolean isCircleViewVisible = false;
        if(mCircleView == null) {
            ensureImageView();
        }
        if(mCircleView != null) {
            isCircleViewVisible = mCircleView.getBottom() > 0;
        }

        if(dy > 0 && offsetY != headerHeight && !isCircleViewVisible) {  //向上滑动，并且头部数据还没有全部隐藏，需要特殊处理
            if(offsetYTemp > headerHeight) {
                final int consumedY = headerHeight - offsetY;
                offsetY = headerHeight;
                requestLayout(-consumedY);
                consumed[1] = consumedY;
            } else {
                final int consumedY = dy;
                offsetY = offsetYTemp;
                requestLayout(-consumedY);
                consumed[1] = consumedY;
            }
        } else {
            super.onNestedPreScroll(target, dx, dy, consumed);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "onNestedScroll");
        final int offsetYTemp = offsetY + dyUnconsumed;

        if(dyUnconsumed < 0 && offsetY != 0) { //向下滑动，并且头部数据还没有全部展示出来，需要特殊处理
            if(offsetYTemp < 0) {
                final int consumedY = offsetY;
                final int unconsumedY = offsetYTemp;
                offsetY = 0;
                requestLayout(consumedY);
                super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, unconsumedY);
            } else {
                final int consumedY = dyUnconsumed;
                offsetY = offsetYTemp;
                requestLayout(-consumedY);
            }
        } else {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        Log.d(TAG, "onNestedPreFling"
            + "  getTop =  " + mTarget.getTop()
            + "  canChildScrollUp = " + canChildScrollUp()
            + "  canViewScrollUp = " + canViewScrollUp(target));
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        Log.d(TAG, "onNestedFling");
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * 重置所有子View的位置
     * @param offsetY
     */
    private void requestLayout(int offsetY) {
        for (int i = 0; i < getChildCount(); i ++){
            if(i == 1)continue;
            View childView = getChildAt(i);
            childView.offsetTopAndBottom(offsetY);
        }
        requestLayout();
    }
}
