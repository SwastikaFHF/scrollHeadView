package com.aitangba.testproject.view.removeview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RemoveViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(inflater.getContext());
        return textView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("RemoveViewActivity", "onViewCreated --- " + ViewCompat.isAttachedToWindow(view));

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.d("RemoveViewActivity", "Handler --- " + ViewCompat.isAttachedToWindow(view));
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("RemoveViewActivity", "onDestroyView --- ");
    }
}
