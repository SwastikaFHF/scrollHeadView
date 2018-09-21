package com.aitangba.testproject.tracktask.adapter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.FragmentActivity;

import com.aitangba.testproject.tracktask.CancelableJob;
import com.aitangba.testproject.tracktask.TaskAdapter;

public class ActivityTaskAdapter implements TaskAdapter, LifecycleObserver {
    private final Lifecycle mLifecycle;
    private CancelableJob mJob;

    public ActivityTaskAdapter(FragmentActivity activity) {
        mLifecycle = activity.getLifecycle();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mLifecycle.removeObserver(this);
        if(mJob != null) {
            mJob.cancel();
        }
    }

    @Override
    public void register(CancelableJob cancelableJob) {
        mJob = cancelableJob;
        mLifecycle.addObserver(this);
    }

    @Override
    public void unregister() {
        mLifecycle.removeObserver(this);
    }
}
