package com.aitangba.testproject.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by fhf11991 on 2018/6/29
 */
public interface TabAdapter {
    void show(int index);
    Fragment getItem(int position);
    int getCount();
}
