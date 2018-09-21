package com.aitangba.testproject.view.lightadapter.viewmodel;

import android.databinding.ViewDataBinding;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2016/10/20.
 */

public interface ViewModel<Ad extends LightAdapter, T extends ViewDataBinding> {

    int getViewType();

    LightViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType);

    void onBindViewHolder(Ad lightAdapter, T dataBinding);
}
