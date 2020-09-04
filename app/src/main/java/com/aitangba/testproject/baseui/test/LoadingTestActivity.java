package com.aitangba.testproject.baseui.test;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.aitangba.testproject.R;
import com.aitangba.testproject.baseui.BaseActivity;
import com.aitangba.testproject.baseui.StatefulViewHelper;
import com.aitangba.testproject.databinding.LayoutCommonViewBinding;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class LoadingTestActivity extends BaseActivity {

    private StatefulViewHelper mStatefulHelper;
    private LayoutCommonViewBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_common_view);

        mStatefulHelper = new StatefulViewHelper(mBinding.view);

        mBinding.firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatefulHelper.showLoading();
            }
        });

        mBinding.secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatefulHelper.dismiss();
            }
        });
    }
}
