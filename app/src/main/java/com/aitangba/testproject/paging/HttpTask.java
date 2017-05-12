package com.aitangba.testproject.paging;

import com.aitangba.testproject.paging.effect.UIEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/5/12.
 */

public class HttpTask {

    private List<UIEffect> mEffects;
    private int pageIndex;

    public HttpTask appendUIEffect(UIEffect uiEffect) {
        if(mEffects == null) {
            mEffects = new LinkedList<>();
        }
        mEffects.add(uiEffect);
        return this;
    }

    public void start() {

    }

    public void onPreExecute(){
        if(mEffects == null) {
            return;
        }

        for(UIEffect uiEffect : mEffects) {
            uiEffect.onPreExecute(this);
        }
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
