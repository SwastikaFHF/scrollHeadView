package com.aitangba.testproject.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/5/25.
 */
public class ViewDemoActivity extends AppCompatActivity {

    private static final String TAG = "ViewDemo";
    private static int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ---");
        setContentView(R.layout.activity_view_demo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy ---");
    }
}
