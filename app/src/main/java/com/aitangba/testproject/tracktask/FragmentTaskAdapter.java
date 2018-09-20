package com.aitangba.testproject.tracktask;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.Fragment;

public class FragmentTaskAdapter implements TrackedAsyncTask.TaskAdapter,LifecycleObserver {

    private Lifecycle mLifecycle;
    private TrackedAsyncTask mAsyncTask;

    public FragmentTaskAdapter(Fragment fragment) {
        mLifecycle = fragment.getLifecycle();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mLifecycle.removeObserver(this);
        if(mAsyncTask != null) {
            mAsyncTask.cancelWithoutCallback();
        }
    }

    @Override
    public void register(TrackedAsyncTask asyncTask) {
        mAsyncTask = asyncTask;
        mLifecycle.addObserver(this);
    }

    @Override
    public void unregister() {
        mLifecycle.removeObserver(this);
    }
}
