package com.aitangba.testproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatViewInflater;
import android.support.v7.widget.AppCompatButton;
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
