package com.aitangba.testproject.view.verticalnestedscroll;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by fhf11991 on 2016/6/22.
 */
public class HeaderRecyclerView extends RecyclerView{
    private static final String TAG = "HeaderRecyclerView";

    public HeaderRecyclerView(Context context) {
        super(context);
    }

    public HeaderRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        Log.d(TAG , "startNestedScroll");
        return super.startNestedScroll(axes);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        Log.d(TAG , "dispatchNestedPreScroll");
        if(isScrollToTop(this) || dy > 0) { //直接处向上滑动事件
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        }
        return false;

//        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        Log.d(TAG , "dispatchNestedPreFling" + "   velocityY = " + velocityY);
        if(isScrollToTop(this) && velocityY < 0) {
            return super.dispatchNestedPreFling(velocityX, velocityY);
        }
        return false;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG , "dispatchNestedFling");
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    private static boolean isScrollToTop(RecyclerView recyclerView) {
        final LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            if(gridLayoutManager.findFirstVisibleItemPosition() == 0
                    && gridLayoutManager.findViewByPosition(0).getTop() == 0 ){
                return true;
            }
        } else if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if(linearLayoutManager.findFirstVisibleItemPosition() == 0
                    && linearLayoutManager.findViewByPosition(0).getTop() == 0 ){
                return true;
            }
        } else if(layoutManager instanceof StaggeredGridLayoutManager){
            // 获取布局管理器
            StaggeredGridLayoutManager layout = (StaggeredGridLayoutManager)layoutManager;
            // 用来记录lastItem的position
            // 由于瀑布流有多个列 所以此处用数组存储
            int column = layout.getColumnCountForAccessibility(null,null);
            int positions[] = new int[column];
            // 获取lastItem的positions

            layout.findFirstVisibleItemPositions(positions);
            for(int i=0;i<positions.length ; i++){
                if(positions[i] == 0 && layout.findViewByPosition(0).getTop() <= 0){
                   return true;
                }
            }
        } else {

        }
        return false;
    }

    private static boolean isScrollToBottom(RecyclerView recyclerView) {
        final LayoutManager layoutManager = recyclerView.getLayoutManager();
        final int bottom = recyclerView.getBottom();

        if(layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            if(gridLayoutManager.findLastVisibleItemPosition() == 0
                    && gridLayoutManager.findViewByPosition(0).getBottom() == bottom ){
                return true;
            }
        } else if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if(linearLayoutManager.findLastVisibleItemPosition() == 0
                    && linearLayoutManager.findViewByPosition(0).getBottom() == bottom ){
                return true;
            }
        } else if(layoutManager instanceof StaggeredGridLayoutManager){
            // 获取布局管理器
            StaggeredGridLayoutManager layout = (StaggeredGridLayoutManager)layoutManager;
            // 用来记录lastItem的position
            // 由于瀑布流有多个列 所以此处用数组存储
            int column = layout.getColumnCountForAccessibility(null,null);
            int positions[] = new int[column];
            // 获取lastItem的positions

            layout.findLastVisibleItemPositions(positions);
            for(int i=0;i<positions.length ; i++){
                /**
                 * 判断lastItem的底边到recyclerView顶部的距离
                 * 是否小于recyclerView的高度
                 * 如果小于或等于 说明滚动到了底部
                 */
                // 刚才忘了写判断是否是最后一个item了
                if(positions[i] >= (layout.getItemCount()-column)
                        && layout.findViewByPosition(positions[i]).getBottom() <= bottom){
                    return true;
                }
            }
        } else {

        }
        return false;
    }
}
