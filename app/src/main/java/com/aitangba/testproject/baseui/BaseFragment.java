package com.aitangba.testproject.baseui;

import android.content.Context;
import androidx.fragment.app.Fragment;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class BaseFragment extends Fragment{

    protected LoadingDialogHelper mLoadingDialogHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BaseActivity) {
            mLoadingDialogHelper = ((BaseActivity)context).mLoadingDialogHelper;
        }
    }
}
