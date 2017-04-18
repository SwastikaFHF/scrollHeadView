package com.aitangba.testproject.stub;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/4/18.
 */

public class CustomView extends BaseViewStub {

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.view_empty, parent, false);
    }
}
