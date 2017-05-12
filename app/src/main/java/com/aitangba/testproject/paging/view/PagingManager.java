package com.aitangba.testproject.paging.view;

/**
 * Created by XBeats on 2017/3/26.
 */

public interface PagingManager {

    void startLoad(boolean refresh);

    void finishLoadMore(boolean hasMoreData);

    void checkPaging(int size);

    int getPageIndex();
}
