package com.aitangba.testproject.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aitangba.testproject.R;

public class LifeCycleActivity extends AppCompatActivity {

    private static final String TAG = "LifeCycleActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLifecycle().addObserver(new Custom());
            }
        });
    }

    private static class Custom implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        private void onCreate(){
            Log.d(TAG, "onCreate");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        private void onStart(){
            Log.d(TAG, "onStart");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        private void onResume(){
            Log.d(TAG, "onResume");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        private void onDestroy(){
            Log.d(TAG, "onDestroy");
        }
    }
}
