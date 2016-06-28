package com.aitangba.scrollheadview.multiadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.scrollheadview.multiadapter.viewholder.BaseViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.CommonViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.EmptyViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.FooterViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.HeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/6/27.
 */
public abstract class MultiAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>{

    private List<T> mList;
    private int mHeadViewSize;

    protected boolean mIsAutoLoadMore = false;
    protected boolean mHasEmptyView = true;
    protected OnLoadMoreListener mOnLoadMoreListener;

    protected int mEmptyViewStatus = EmptyViewHolder.STATUS_NO_DATA;
    protected int mFooterViewStatus = FooterViewHolder.STATUS_LOADING_MORE;

    public MultiAdapter() {
        mList = new ArrayList<>(0);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setEmptyViewStatus(int emptyViewStatus) {
        setEmptyViewStatus(emptyViewStatus, false);
    }

    public void setEmptyViewStatus(int emptyViewStatus, boolean refresh) {
        mEmptyViewStatus = emptyViewStatus;
        if(!refresh)return;
        if(mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    public void setFooterViewStatus(int footerViewStatus) {
        setFooterViewStatus(footerViewStatus, false);
    }

    public void setFooterViewStatus(int footerViewStatus, boolean refresh) {
        mFooterViewStatus = footerViewStatus;
        if(!refresh)return;
        notifyDataSetChanged();
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        mIsAutoLoadMore = autoLoadMore;
    }

    public void setHasEmptyView(boolean hasEmptyView) {
        mHasEmptyView = hasEmptyView;
    }

    public int getHeadViewSize() {
        return mHeadViewSize;
    }

    public void setHeadViewSize(int headViewSize) {
        mHeadViewSize = headViewSize;
    }

    public void setData(List<T> list) {
        if(mList == null) {
            mList = new ArrayList<>();
        } else {
           mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addToFoot(List<T> list) {
        if(mList == null) {
            mList = new ArrayList<>();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        final int headViewSize = mHeadViewSize;
        final int listSize = mList == null ? 0 : mList.size();
        final int footerViewSize = 1;
        final int emptyViewSize = 1;
        final boolean hasEmptyView = mHasEmptyView;
        final boolean needFooterView = mIsAutoLoadMore;

        int count;
        if(listSize <= 0 && needFooterView && hasEmptyView) {
            count = headViewSize + listSize + emptyViewSize;
        } else if(listSize <= 0 && needFooterView && !hasEmptyView) {
            count = headViewSize + listSize + footerViewSize;
        } else if(listSize > 0 && needFooterView) {
            count = headViewSize + listSize + footerViewSize;
        } else if(listSize > 0 && !needFooterView) {
            count = headViewSize + listSize;
        } else {
            count = headViewSize + listSize + footerViewSize;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        ItemType itemType;

        final boolean hasEmptyView = mHasEmptyView;
        final boolean isLastPosition = position == getItemCount() - 1;
        final boolean needFooterView = mIsAutoLoadMore;
        final boolean isListEmpty = mList == null ? true : mList.size() == 0;

        if(position < mHeadViewSize) {
            itemType = ItemType.HeaderView;
        } else if(position == mHeadViewSize && !isListEmpty) {
            itemType = ItemType.CommonView;
        } else if(position == mHeadViewSize && isListEmpty && hasEmptyView) {
            itemType = ItemType.EmptyView;
        } else if(position == mHeadViewSize && isListEmpty && !hasEmptyView) {
            itemType = needFooterView ? ItemType.FooterView : ItemType.CommonView;
        } else if(position == mHeadViewSize && !hasEmptyView && isLastPosition) {
            itemType = needFooterView ? ItemType.FooterView : ItemType.CommonView;
        } else if(position == mHeadViewSize && !hasEmptyView && !isLastPosition) {
            itemType = ItemType.CommonView;
        } else if(position > mHeadViewSize && isLastPosition) {
            itemType = needFooterView ? ItemType.FooterView : ItemType.CommonView;
        } else if(position > mHeadViewSize && !isLastPosition) {
            itemType = ItemType.CommonView;
        } else {
            itemType = ItemType.CommonView;
        }

        int itemViewType;

        if(itemType == ItemType.HeaderView) {
            itemViewType = ItemType.HeaderView.itemType | getViewType(ItemType.HeaderView, position);
        } else if(itemType == ItemType.CommonView) {
            itemViewType = ItemType.CommonView.itemType | getViewType(ItemType.CommonView, position);
        } else {
            itemViewType = itemType.itemType;
        }
        return itemViewType;
    }

    public int getViewType(ItemType itemType, int position) {
        return itemType.itemType;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int itemViewType = viewType & 0xffff0000;
        final int realViewType = viewType & 0x0000ffff;
        final ItemType itemType = ItemType.getItemType(itemViewType);

        return onCreateViewHolder(parent, itemType, realViewType);
    }

    protected BaseViewHolder onCreateViewHolder(ViewGroup parent, ItemType itemType, int viewType) {
        BaseViewHolder baseViewHolder = null;
        switch (itemType){
            case HeaderView:
                baseViewHolder = new HeaderViewHolder(new View(parent.getContext()));
                break;
            case FooterView:
                baseViewHolder = new FooterViewHolder(new View(parent.getContext()), mOnLoadMoreListener);
                break;
            case EmptyView:
                baseViewHolder = new EmptyViewHolder(parent, new View(parent.getContext()));
                break;
            case CommonView:
                baseViewHolder = onCreateCommonViewHolder(parent, viewType);
            default:
                break;
        }
        return baseViewHolder;
    }

    protected abstract CommonViewHolder onCreateCommonViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final ItemType itemType = holder.getItemType();
        switch (itemType) {
            case HeaderView:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.onBindViewHolder(position);
                break;
            case FooterView:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                footerViewHolder.dealFooterStatus(mFooterViewStatus);
                break;
            case EmptyView:
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                emptyViewHolder.dealEmptyStatus(mEmptyViewStatus);
                break;
            case CommonView:
                CommonViewHolder<T> commonViewHolder = (CommonViewHolder<T>) holder;
                commonViewHolder.onBindViewHolder(mList.get(position - mHeadViewSize));
                break;
            default:break;
        }
    }

    public interface OnLoadMoreListener {
        void onLoad();
    }
}