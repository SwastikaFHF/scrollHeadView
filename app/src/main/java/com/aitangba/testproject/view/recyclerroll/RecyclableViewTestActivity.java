package com.aitangba.testproject.view.recyclerroll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aitangba.testproject.R;

/**
 * Created by XBeats on 2020/4/29
 * https://github.com/Marksss/InfiniteBanner
 */
public class RecyclableViewTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclable_view);

        RecyclableViewGroup recyclableViewGroup = findViewById(R.id.recyclableViewGroup);
    }
}
