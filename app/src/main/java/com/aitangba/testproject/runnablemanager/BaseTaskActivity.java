package com.aitangba.testproject.runnablemanager;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/10/31.
 */

public class BaseTaskActivity extends AppCompatActivity implements TaskManager {

    private List<UiTask> tasks = new ArrayList<>();

    private RunQueue runQueue = new RunQueue();

    @Override
    public List<UiTask> getTasks() {
        return tasks;
    }

    @Override
    public View getRootView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    @Override
    public void addTask(UiTask task, long delayMillis) {
        runQueue.postDelayed(task, delayMillis);
    }

    @Override
    protected void onDestroy() {
        tasks.clear();
        super.onDestroy();
    }
}
