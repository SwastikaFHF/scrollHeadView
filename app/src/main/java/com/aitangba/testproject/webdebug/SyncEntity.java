package com.aitangba.testproject.webdebug;

import com.google.gson.Gson;

/**
 * Created by zf08526 on 2015/7/28.
 */
public class SyncEntity<T>{
    private T params;

    public T getParams(){
        // auto convert
        return params;
    }

    public void setParams(T params){
        this.params = params;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
