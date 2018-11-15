package com.aitangba.testproject.paging;

import java.util.ArrayList;
import java.util.List;

public class Response {
    public String code; //"101",
    public String msg; //"获取成功",
    public List array = new ArrayList(); //[],
    public String obj; //{}
    public int pageIndx; // 1,
    public int totalPage; // 10,
}
