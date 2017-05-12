package com.aitangba.testproject.paging.view.adpater;

import com.aitangba.testproject.paging.view.PagingManager;

import java.util.List;

/**
 * Created by fhf11991 on 2017/5/12.
 */

public interface AdapterManager {
    void addData(PagingManager pagingManager, List list, boolean refresh);
}
