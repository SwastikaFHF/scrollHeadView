package com.aitangba.testproject.tracktask.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.aitangba.testproject.R;
import com.aitangba.testproject.invisiblefragment.task.HttpTask;

/**
 * Created by fhf11991 on 2018/4/24.
 */

public class TrackedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        TrackedFragment fragment = new TrackedFragment();
        transaction.add(R.id.containerLayout, fragment).commit();

        new HttpTask(this){

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return super.doInBackground(objects);
            }

            @Override
            protected void onSuccess(Object o) {
                super.onSuccess(o);
                Toast.makeText(TrackedActivity.this, "Activity成功啦！！！", Toast.LENGTH_SHORT).show();
            }
        }.startRequest();
    }
}
