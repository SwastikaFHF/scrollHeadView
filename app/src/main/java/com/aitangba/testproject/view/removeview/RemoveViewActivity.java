package com.aitangba.testproject.view.removeview;

import android.arch.lifecycle.LifecycleObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aitangba.testproject.R;

public class RemoveViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.d("RemoveViewActivity", "验证handler机制 ---");
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_view);
        Button button = findViewById(R.id.btn);
        TextView textView = findViewById(R.id.text);
        getLifecycle().addObserver(new LifecycleObserver() {
        });

//        button.post(new Runnable() {
//            @Override
//            public void run() {
//                ViewGroup parent = (ViewGroup) textView.getParent();
//                parent.removeView(textView);
//            }
//        });
        textView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        Fragment fragment = new RemoveViewFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, fragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();


    }


    protected void onResume() {
        super.onResume();
        Log.d("RemoveViewActivity", "onResume ---");
    }
}
