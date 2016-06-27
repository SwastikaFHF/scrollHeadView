package com.aitangba.scrollheadview.multiadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/6/27.
 */
public abstract class MultiAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>{

    private List<T> mList;
    private int mHeadViewSize;
    private View mEmptyView;
    private int mEmptyViewId;
    private boolean mIsAutoLoadMore;
    private OnLoadMoreListener mOnLoadMoreListener;

    public MultiAdapter() {
        mList = new ArrayList<>(0);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        mIsAutoLoadMore = autoLoadMore;
    }

    public int getHeadViewSize() {
        return mHeadViewSize;
    }

    public void setHeadViewSize(int headViewSize) {
        mHeadViewSize = headViewSize;
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    public void setEmptyView(int emptyViewId) {
        mEmptyViewId = emptyViewId;
    }

    public MultiAdapter(List<T> list) {
        if(mList == null) {
            mList = new ArrayList<>();
        }
        mList = list;
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
    public int getItemViewType(int position) {
        ItemType itemType;

        final boolean hasEmptyView = mEmptyView != null || mEmptyViewId > 0;
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
            itemViewType = getHeadViewType(position);
        } else if(itemType == ItemType.CommonView) {
            itemViewType = getCommonViewType(position - mHeadViewSize);
        } else {
            itemViewType = itemType.itemType;
        }
        return itemViewType;
    }

    public int getHeadViewType(int position) {
        return  ItemType.HeaderView.itemType;
    }

    public int getCommonViewType(int position) {
        return  ItemType.CommonView.itemType;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        BaseViewHolder baseViewHolder;

        if(viewType == ItemType.HeaderView.itemType) {
            baseViewHolder = onCreateHeadViewHolder(parent, viewType);
        } else if(viewType == ItemType.EmptyView.itemType) {
            baseViewHolder = onCreateEmptyViewHolder(parent, viewType);
        } else if(viewType == ItemType.FooterView.itemType) {
            baseViewHolder = onCreateFooterViewHolder(parent, viewType);
        } else {
            baseViewHolder = onCreateCommonViewHolder(parent, viewType);
        }
        return baseViewHolder;
    }

    protected HeaderViewHolder onCreateHeadViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setText("我是头部");
        return new HeaderViewHolder(textView);
    }

    protected abstract CommonViewHolder onCreateCommonViewHolder(ViewGroup parent, int viewType);

    protected FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setText("我是尾部");
        return new FooterViewHolder(textView);
    }

    protected EmptyViewHolder onCreateEmptyViewHolder(ViewGroup parent, int viewType) {
        return new EmptyViewHolder(getEmptyView(parent));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
         if(holder instanceof CommonViewHolder) {
             CommonViewHolder<T> commonViewHolder = (CommonViewHolder<T>) holder;
             commonViewHolder.onBindViewHolder(mList.get(position - mHeadViewSize));
         } else if (holder instanceof FooterViewHolder) {
             if(mOnLoadMoreListener != null)mOnLoadMoreListener.onLoad();
         }
    }

    @Override
    public int getItemCount() {
        final int headViewSize = mHeadViewSize;
        final int listSize = mList == null ? 0 : mList.size();
        final int footerViewSize = 1;
        final int emptyViewSize = 1;
        final boolean hasEmptyView = mEmptyView != null || mEmptyViewId > 0;
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

    private View getEmptyView(ViewGroup viewGroup) {
        View emptyView;
        if(mEmptyViewId > 0) {
            emptyView = LayoutInflater.from(viewGroup.getContext()).inflate(mEmptyViewId, viewGroup, false);
        } else if(mEmptyView != null) {
            emptyView = mEmptyView;
        } else {
            emptyView = new View(viewGroup.getContext());
        }
        int topMargin = 0;
        for(int i= 0; i < viewGroup.getChildCount(); i++) {
            View childView = viewGroup.getChildAt(i);
            topMargin = topMargin + childView.getMeasuredHeight();
        }
        int height = viewGroup.getMeasuredHeight() - topMargin;
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        return emptyView;
    }

    public enum ItemType {
        HeaderView(0xf1), CommonView(0xf2), FooterView(0xf3), EmptyView(0xf4);
        public final int itemType;
        ItemType (int itemType){
             this.itemType = itemType;
        }
    }

    public interface OnLoadMoreListener {
        void onLoad();
    }
}

class HeaderViewHolder extends BaseViewHolder {

    public HeaderViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public MultiAdapter.ItemType getItemType() {
        return MultiAdapter.ItemType.HeaderView;
    }
}

class EmptyViewHolder extends BaseViewHolder {

    public EmptyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public MultiAdapter.ItemType getItemType() {
        return MultiAdapter.ItemType.EmptyView;
    }
}

abstract class CommonViewHolder<Data> extends BaseViewHolder {

    public CommonViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindViewHolder(Data date);

    @Override
    public MultiAdapter.ItemType getItemType() {
        return MultiAdapter.ItemType.CommonView;
    }
}

class FooterViewHolder extends BaseViewHolder {

    public FooterViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public MultiAdapter.ItemType getItemType() {
        return MultiAdapter.ItemType.FooterView;
    }
}

abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract MultiAdapter.ItemType getItemType();

}
