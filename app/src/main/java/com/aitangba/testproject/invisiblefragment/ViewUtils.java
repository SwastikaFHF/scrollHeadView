package com.aitangba.testproject.invisiblefragment;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;

import java.util.HashMap;
import java.util.Map;

public class ViewUtils {

    public static FragmentManager findFragmentManager(View view) {
        FragmentActivity fragmentActivity = getActivityFromView(view);
        FragmentManager fragmentManager = null;
        if (fragmentActivity != null) {
            Map<View, FragmentManager> map = new HashMap<>();
            View contentView = fragmentActivity.findViewById(Window.ID_ANDROID_CONTENT);
            if (contentView != null) {
                map.put(contentView, fragmentActivity.getSupportFragmentManager());
            }

            for (Fragment childFragment : fragmentActivity.getSupportFragmentManager().getFragments()) {
                View rootView = childFragment.getView();
                if (rootView != null) {
                    map.put(rootView, childFragment.getChildFragmentManager());
                }
            }

            View tempView = view;

            while (tempView != null) {
                for (Map.Entry<View, FragmentManager> entry : map.entrySet()) {
                    if (entry.getKey() == tempView) {
                        fragmentManager = entry.getValue();
                        break;
                    }
                }
                if(tempView.getParent() instanceof View) {
                    tempView = (View) tempView.getParent();
                } else {
                    break;
                }
            }
        }
        return fragmentManager;
    }

    public static Lifecycle findLifecyle(View view) {
        FragmentActivity fragmentActivity = getActivityFromView(view);
        Lifecycle lifecycle = null;
        if (fragmentActivity != null) {
            Map<View, Lifecycle> map = new HashMap<>();
            View contentView = fragmentActivity.findViewById(Window.ID_ANDROID_CONTENT);
            if (contentView != null) {
                map.put(contentView, fragmentActivity.getLifecycle());
            }

            for (Fragment childFragment : fragmentActivity.getSupportFragmentManager().getFragments()) {
                View rootView = childFragment.getView();
                if (rootView != null) {
                    map.put(rootView, childFragment.getLifecycle());
                }
            }

            View tempView = view;

            while (tempView != null) {
                for (Map.Entry<View, Lifecycle> entry : map.entrySet()) {
                    if (entry.getKey() == tempView) {
                        lifecycle = entry.getValue();
                        break;
                    }
                }
                if(tempView.getParent() instanceof View) {
                    tempView = (View) tempView.getParent();
                } else {
                    break;
                }
            }
        }
        return lifecycle;
    }

    public static FragmentActivity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof FragmentActivity) {
                return (FragmentActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
