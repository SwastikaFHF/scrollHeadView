package com.aitangba.testproject.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ActivityFragmentBinding;

/**
 * Created by fhf11991 on 2017/8/9.
 */

public class FragmentTestActivity extends AppCompatActivity {

    private ActivityFragmentBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_fragment);

        mBinding.testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(R.id.container_layout, new FirstFragment(), "cc").commitAllowingStateLoss();
                finish();
            }
        });

    }
}
