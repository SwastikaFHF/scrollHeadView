package com.aitangba.testproject.view.keyboard;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/6/28
 */
public class KeyboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        EditText editText = findViewById(R.id.editText);

        IDCardInputHelper.with(this).bind(editText);
    }
}
