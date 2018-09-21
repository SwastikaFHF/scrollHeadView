package com.aitangba.testproject.tracktask.adapter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.aitangba.testproject.tracktask.CancelableJob;
import com.aitangba.testproject.tracktask.TaskAdapter;

import java.util.HashMap;
import java.util.Map;

public class ViewTaskAdapter implements TaskAdapter, View.OnAttachStateChangeListener, LifecycleObserver {

    private View mView;
    private Lifecycle mLifecycle;
    private CancelableJob mJob;

    public ViewTaskAdapter(View view) {
        mView = view;

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
                tempView = (View) tempView.getParent();
            }
        }
        mLifecycle = lifecycle;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mLifecycle.removeObserver(this);
        if(mJob != null) {
            mJob.cancel();
        }
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        v.removeOnAttachStateChangeListener(this);
        if(mJob != null) {
            mJob.cancel();
        }
    }

    private FragmentActivity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof FragmentActivity) {
                return (FragmentActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @Override
    public void register(CancelableJob cancelableJob) {
        mJob = cancelableJob;
        if (mLifecycle != null) {
            mLifecycle.addObserver(this);
        } else {
            mView.addOnAttachStateChangeListener(this);
        }
    }

    @Override
    public void unregister() {
        if (mLifecycle != null) {
            mLifecycle.removeObserver(this);
        } else {
            mView.removeOnAttachStateChangeListener(this);
        }
    }
}
