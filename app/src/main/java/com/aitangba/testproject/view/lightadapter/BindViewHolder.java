package com.aitangba.testproject.view.lightadapter;

import android.support.v7.widget.RecyclerView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by fhf11991 on 2016/10/17.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BindViewHolder {
    Class<? extends RecyclerView.ViewHolder> bindViewHolder();
}
