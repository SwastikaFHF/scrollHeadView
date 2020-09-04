package com.aitangba.testproject;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatViewInflater;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by XBeats on 2020/3/30
 */
public class ViewInflaterClass extends AppCompatViewInflater {


    @NonNull
    @Override
    protected AppCompatButton createButton(Context context, AttributeSet attrs) {
        Log.d("ViewInflaterClass", "createButton -- " + context.getClass().getName());
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.View, 0, 0);
        return super.createButton(context, attrs);
    }
}
