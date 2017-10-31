package com.aitangba.testproject.runnablemanager;

import android.view.View;

import java.util.List;

/**
 * Created by fhf11991 on 2017/10/31.
 */

public interface TaskManager {

    List<UiTask> getTasks();

    View getRootView();

    void addTask(UiTask task, long delayMillis);
}
