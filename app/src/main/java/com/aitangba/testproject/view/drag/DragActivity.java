package com.aitangba.testproject.view.drag;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

        dragFrameLayout.setCardView(findViewById(R.id.text1));
    }
}
