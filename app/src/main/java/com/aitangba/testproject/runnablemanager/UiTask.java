package com.aitangba.testproject.runnablemanager;

/**
 * Created by fhf11991 on 2017/10/31.
 */

public class UiTask implements Runnable {

    private TaskManager mTaskManager;

    public UiTask(TaskManager taskManager) {
        this.mTaskManager = taskManager;
    }

    public void post() {
        postDelay(0);
    }

    public void postDelay(long delayMillis) {
        mTaskManager.addTask(this, delayMillis);
    }

    @Override
    public void run() {

    }
}
