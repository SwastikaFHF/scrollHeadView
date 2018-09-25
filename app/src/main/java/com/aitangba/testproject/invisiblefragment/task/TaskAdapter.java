package com.aitangba.testproject.invisiblefragment.task;

public interface TaskAdapter {
    void register(CancelableJob cancelableJob);
    void unregister();
}
