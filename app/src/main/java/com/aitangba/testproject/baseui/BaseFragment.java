package com.aitangba.testproject.baseui;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class BaseFragment extends Fragment{

    protected DialogManager dialogManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DialogManager) {
            dialogManager = (DialogManager)context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialogManager = null;
    }
}
