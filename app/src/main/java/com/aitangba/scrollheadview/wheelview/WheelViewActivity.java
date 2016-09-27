package com.aitangba.scrollheadview.wheelview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aitangba.scrollheadview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/9/27.
 */

public class WheelViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);
        WheelView wheelView = (WheelView) findViewById(R.id.view_wheel);
        wheelView.setList(getDate(10));
    }

    private List<String> getDate(int size) {
        List<String> list = new ArrayList<>();
        for(int i = 0 ; i < size ; i++) {
            list.add("数据" + i);
        }
        return list;
    }
}
