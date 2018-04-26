package com.aitangba.testproject.tracktask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/4/24.
 */

public class TrackedFragment extends Fragment {

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_one, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("TrackedActivity", "runnable -- getActivity() == null ?? " + (getActivity() == null));
            }
        });
        Log.d("TrackedActivity", "onDestroyView -- getActivity() == null ?? " + (getActivity() == null));
    }
}

