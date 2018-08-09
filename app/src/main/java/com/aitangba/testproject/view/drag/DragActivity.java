package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/8/6
 */
public class DragActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

        DragFrameLayout dragFrameLayout = findViewById(R.id.dragLayout);
        dragFrameLayout.bindImageView(findViewById(R.id.image));

        ObserverSizeTextView textView = findViewById(R.id.text2);
        dragFrameLayout.bindTextView(findViewById(R.id.text2));

        NestedScrollView scrollView = findViewById(R.id.scrollView);
        dragFrameLayout.bindNestedScrollView(scrollView);

    }

    private static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5F;
    }
}
