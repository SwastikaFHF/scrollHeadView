package com.aitangba.testproject.runnablemanager;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;

import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ActivityRunnableManagerBinding;

/**
 * Created by fhf11991 on 2017/10/31.
 */

public class RunnableManagerActivity extends BaseTaskActivity {

    private final static String TAG = "Runnable";
    private ActivityRunnableManagerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_runnable_manager);

        findViewById(Window.ID_ANDROID_CONTENT).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.text.setText("你好");
                Log.d(TAG, "4s时间到了....");
            }
        }, 4000);
    }
}
