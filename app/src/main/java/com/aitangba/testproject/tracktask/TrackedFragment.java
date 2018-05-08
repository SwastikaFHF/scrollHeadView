package com.aitangba.testproject.tracktask;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/4/24.
 */

public class TrackedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_one, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new HttpTask(this){

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return super.doInBackground(objects);
            }

            @Override
            protected void onSuccess(Object o) {
                super.onSuccess(o);
                Toast.makeText(getActivity(), "成功啦！！！", Toast.LENGTH_SHORT).show();
            }
        }.startRequest();
    }
}

