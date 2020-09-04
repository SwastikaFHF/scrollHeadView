package com.aitangba.testproject.view.drag;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/8/6
 */
public class DragActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

//        DragFrameLayout dragFrameLayout = findViewById(R.id.dragLayout);
//        dragFrameLayout.bindImageView(findViewById(R.id.image));
//
        ObserverSizeTextView textView = findViewById(R.id.text2);
//        dragFrameLayout.bindTextView(findViewById(R.id.text2));
//
//        NestedScrollView scrollView = findViewById(R.id.scrollView);
//        dragFrameLayout.bindNestedScrollView(scrollView);

//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "触发了点击", Toast.LENGTH_SHORT).show();
//            }
//        });

//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.setScrollY((int) dp2px(scrollView.getContext(), 100));
//            }
//        });

        ScrollChildView childView = findViewById(R.id.scrollChildView);
        childView.bindImageView(findViewById(R.id.image));
        childView.bindTextView(findViewById(R.id.text2));
    }
}
