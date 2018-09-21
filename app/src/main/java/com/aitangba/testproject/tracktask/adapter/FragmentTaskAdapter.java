package com.aitangba.testproject.tracktask.adapter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.Fragment;

import com.aitangba.testproject.tracktask.CancelableJob;
import com.aitangba.testproject.tracktask.TaskAdapter;

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
