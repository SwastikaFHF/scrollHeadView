package com.aitangba.scrollheadview.multiadapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.aitangba.scrollheadview.multiadapter.ItemType;
import com.aitangba.scrollheadview.multiadapter.MultiAdapter;

/**
 * Created by fhf11991 on 2016/6/28.
 */
public class FooterViewHolder extends BaseViewHolder {

    public static final int STATUS_NONE = 1; //准备状态
    public static final int STATUS_LOADING_MORE = 2; //正在加载中
    public static final int STATUS_NO_MORE = 3;  //没有更多数据

    private MultiAdapter.OnLoadMoreListener mOnLoadMoreListener;

    private int mCurrentStatus = STATUS_NONE;

    public FooterViewHolder(View itemView, MultiAdapter.OnLoadMoreListener onLoadMoreListener) {
        super(itemView);
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setOnLoadMoreListener(MultiAdapter.OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.FooterView;
    }

    public void dealFooterStatus(int status){
        if((mCurrentStatus == status))return;

        mCurrentStatus = status;
        if(mCurrentStatus == STATUS_NONE) {
        } else if(mCurrentStatus == STATUS_LOADING_MORE) {
            if(mOnLoadMoreListener != null)mOnLoadMoreListener.onLoad();
        } else if(mCurrentStatus == STATUS_NO_MORE) {
            if(itemView instanceof TextView) {
                ((TextView)itemView).setText("没有更多数据了");
            }
        } else {}
    }
}
