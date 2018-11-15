package com.aitangba.testproject.paging;

/**
 * Created by XBeats on 2017/3/26.
 */

public class PageBean {

    private final static int ORIGIN_PAGE_INDEX = 1;
    public final static int ORIGIN_PAGE_SIZE = 20;

    public int pageIndex = ORIGIN_PAGE_INDEX;
    public int pageSize = ORIGIN_PAGE_SIZE;

    public void reset() {
        pageIndex = ORIGIN_PAGE_INDEX;
        pageSize = ORIGIN_PAGE_SIZE;
    }

    public void increase() {
        pageIndex = pageIndex + 1;
    }

    public void decline() {
        pageIndex = Math.max(ORIGIN_PAGE_INDEX, pageIndex - 1);
    }
}
