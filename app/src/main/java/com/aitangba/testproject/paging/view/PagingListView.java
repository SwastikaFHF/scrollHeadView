package com.aitangba.testproject.paging.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.PageBean;


/**
 * Created by XBeats on 2017/3/26.
 */

public class PagingListView extends ListView implements PagingManager {

    private OnLoadMoreListener mLoadMoreListener;
    private boolean isLoadingMore = false;// 是否正在加载
    private boolean mHasMoreData = true;
    private String noDataTips;
    private View progressBar;
    private TextView loadTisText;
    private OnScrollListener mOnScrollListener;
    private boolean hasTouchedScrollView = false; // 是否有触发滑动机制

    private final PageBean mPageBean = new PageBean();

    public PagingListView(Context context) {
        this(context, null);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        progressBar = footerView.findViewById(R.id.footer_view_progressbar);
        loadTisText = (TextView) footerView.findViewById(R.id.footer_view_tv);
        addFooterView(footerView);

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }

                if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hasTouchedScrollView = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                if(!hasTouchedScrollView) return;

                if(isLoadingMore) { //
                    return;
                }

                if(!mHasMoreData) {
                    return;
                }

                if (view.getLastVisiblePosition() + 1 ==  view.getCount()) {
                    if (mLoadMoreListener != null) {
                        isLoadingMore = true;
                        mLoadMoreListener.onLoadMore(false);
                    }
                }
            }
        });
    }

    public void setNoDataTips(String noDataTips) {
        this.noDataTips = noDataTips;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    private void notifyFooterView() {
        if(mHasMoreData) {
            loadTisText.setText("加载更多数据中");
            progressBar.setVisibility(VISIBLE);
        } else {
            if(!TextUtils.isEmpty(noDataTips)) {
                loadTisText.setText(noDataTips);
            } else {
                loadTisText.setText("暂无更多信息");
            }
            progressBar.setVisibility(GONE);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }


    @Override
    public void startLoad(boolean refresh) {
        if(refresh) {
            mPageBean.reset();
        } else {
            mPageBean.increase();
        }
    }

    @Override
    public void finishLoadMore(boolean hasMoreData) {
        isLoadingMore = false;
        notifyFooterView();
    }

    @Override
    public void checkPaging(int size) {
        finishLoadMore(size == PageBean.PAGE_SIZE);
    }

    @Override
    public int getPageIndex() {
        return mPageBean.currentPage;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mLoadMoreListener = mOnLoadMoreListener;
    }
}
