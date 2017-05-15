package com.aitangba.testproject.paging.view;

import com.aitangba.testproject.paging.PageBean;

/**
 * Created by XBeats on 2017/3/26.
 */

public interface PagingManager {

    void setAutoLoadEnabled(boolean enable);

    void startLoad(boolean refresh);

    void finishLoadMore(boolean hasMoreData);

    PageBean getPageBean();

}
