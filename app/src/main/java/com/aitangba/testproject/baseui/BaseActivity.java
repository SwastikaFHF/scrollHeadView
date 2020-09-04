package com.aitangba.testproject.baseui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class BaseActivity extends AppCompatActivity {

    protected LoadingDialogHelper mLoadingDialogHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingDialogHelper = new LoadingDialogHelper(this);
    }
}
