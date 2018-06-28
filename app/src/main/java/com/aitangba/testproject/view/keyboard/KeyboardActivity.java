package com.aitangba.testproject.view.keyboard;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/6/28
 */
public class KeyboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Keyboard keyboard = new Keyboard(this, R.xml.number);
    }
}
