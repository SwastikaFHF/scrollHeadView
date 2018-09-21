package com.aitangba.testproject.tracktask;

public interface TaskAdapter {
    void register(CancelableJob cancelableJob);
    void unregister();
}
