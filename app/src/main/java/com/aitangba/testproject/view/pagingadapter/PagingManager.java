package com.aitangba.testproject.view.pagingadapter;

/**
 * Created by XBeats on 2017/3/26.
 */

public interface PagingManager {

    void setNeverLoadMore(boolean neverLoadMore);

    void finishLoadMore();

    void scrollLoadMore();

    void setOnDataChangedListener(OnDataChangeListener dataChangedListener);

}
