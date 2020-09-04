package com.aitangba.testproject.invisiblefragment.task.adapter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.fragment.app.FragmentActivity;

import com.aitangba.testproject.invisiblefragment.task.CancelableJob;
import com.aitangba.testproject.invisiblefragment.task.TaskAdapter;

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
