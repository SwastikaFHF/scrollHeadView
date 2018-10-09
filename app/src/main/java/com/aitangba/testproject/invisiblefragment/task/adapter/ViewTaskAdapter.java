package com.aitangba.testproject.invisiblefragment.task.adapter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.view.View;

import com.aitangba.testproject.invisiblefragment.ViewUtils;
import com.aitangba.testproject.invisiblefragment.task.CancelableJob;
import com.aitangba.testproject.invisiblefragment.task.TaskAdapter;

public class ViewTaskAdapter implements TaskAdapter, View.OnAttachStateChangeListener, LifecycleObserver {

    private View mView;
    private Lifecycle mLifecycle;
    private CancelableJob mJob;

    public ViewTaskAdapter(View view) {
        mView = view;
        mLifecycle = ViewUtils.findLifecycle(view);
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
