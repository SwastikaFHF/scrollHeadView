package com.aitangba.testproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Created by fhf11991 on 2017/7/21.
 */

public class MainTabAdapter implements TabAdapter {

    public static final String EXTRA_PARAMS = "StartCommand";
    private FragmentManager mFragmentManager;
    private View mContainer;

    public MainTabAdapter(FragmentManager fm, View container) {
        mFragmentManager = fm;
        mContainer = container;
    }

    @Override
    public void show(int index) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        boolean buildFormCache = false;
        String name = makeFragmentName(mContainer.getId(), index);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if(fragment == null) {
            fragment = getItem(index);
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
            fragmentTransaction.add(mContainer.getId(), fragment, name);
        } else {
            fragmentTransaction.attach(fragment);
            buildFormCache = true;
        }

        for(int i = 0,count = getCount() ; i < count ; i ++) {
            if(i == index) {
                continue;
            } else {
                String otherItemName = makeFragmentName(mContainer.getId(), i);
                Fragment otherFragment = mFragmentManager.findFragmentByTag(otherItemName);
                if(otherFragment != null) {
                    fragmentTransaction.hide(otherFragment);
                    otherFragment.setUserVisibleHint(false);
                    otherFragment.setMenuVisibility(false);
                }
            }
        }
        fragmentTransaction.show(fragment);
        fragment.setUserVisibleHint(true);
        fragment.setMenuVisibility(true);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FirstFragment();
        }

        return new SecondFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
