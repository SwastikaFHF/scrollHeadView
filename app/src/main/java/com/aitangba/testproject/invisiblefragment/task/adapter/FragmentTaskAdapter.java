package com.aitangba.testproject.invisiblefragment.task.adapter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.fragment.app.Fragment;

import com.aitangba.testproject.invisiblefragment.task.CancelableJob;
import com.aitangba.testproject.invisiblefragment.task.TaskAdapter;

public class FragmentTaskAdapter implements TaskAdapter,LifecycleObserver {

    private Lifecycle mLifecycle;
    private CancelableJob mJob;

    public FragmentTaskAdapter(Fragment fragment) {
        mLifecycle = fragment.getLifecycle();
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
