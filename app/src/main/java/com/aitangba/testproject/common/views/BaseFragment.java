package com.aitangba.testproject.common.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2016/10/26.
 */

public abstract class BaseFragment extends Fragment {

    private View mRootView;
    private boolean mIsFirstVisibility = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView == null) {
            mRootView = onCreateRootView(inflater, container, savedInstanceState);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public abstract View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser || mRootView == null) {
            return;
        }

        onVisibleToUser(mIsFirstVisibility);
        if(mIsFirstVisibility) {
            mIsFirstVisibility = false;
        }
    }

    public void onVisibleToUser(boolean isFirstVisibility) {

    }
}
