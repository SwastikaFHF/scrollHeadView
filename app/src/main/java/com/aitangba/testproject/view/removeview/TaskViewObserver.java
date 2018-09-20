package com.aitangba.testproject.view.removeview;

import android.view.View;

import com.aitangba.testproject.tracktask.HttpTask;

public class TaskViewObserver implements View.OnAttachStateChangeListener {

    private HttpTask mHttpTask;

    public TaskViewObserver(HttpTask httpTask) {
        mHttpTask = httpTask;
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        v.removeOnAttachStateChangeListener(this);
        mHttpTask.cancelWithoutCallback();
    }
}
