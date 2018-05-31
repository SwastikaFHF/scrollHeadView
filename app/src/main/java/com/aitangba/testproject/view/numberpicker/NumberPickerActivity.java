package com.aitangba.testproject.view.numberpicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.aitangba.testproject.R;


/**
 * Created by Carbs.Wang on 2016/6/24.
 */
public class NumberPickerActivity extends AppCompatActivity {

    private NumberPickerView mPickerViewH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_picker);
        mPickerViewH = (NumberPickerView) this.findViewById(com.aitangba.testproject.R.id.picker_hour);

        mPickerViewH.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {

            }
        });

        mPickerViewH.setDisplayedValues(getResources().getStringArray(R.array.hour_display));
        setData(mPickerViewH, 0, 11, 0);
    }

    private void setData(NumberPickerView picker, int minValue, int maxValue, int value) {
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(value);
    }
}