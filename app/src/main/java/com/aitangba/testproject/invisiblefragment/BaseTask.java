package com.aitangba.testproject.invisiblefragment;

import android.support.annotation.NonNull;

public abstract class BaseTask<Result, Error> {

    private final TaskAdapter mTaskAdapter;

    public BaseTask(@NonNull TaskAdapter taskAdapter) {
        mTaskAdapter = taskAdapter;
    }

    protected void onCancelled() {
        mTaskAdapter.unregister();
    }

    protected void onError(Error error) {
        mTaskAdapter.unregister();
    }

    protected void onSuccess(Result result) {
        mTaskAdapter.unregister();
    }

    public abstract void start();

    public void cancel() {

    }

    public interface TaskAdapter {
        void register(BaseTask baseTask);
        void unregister();
    }
}
