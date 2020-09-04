package com.aitangba.testproject.view.stub;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.ref.WeakReference;

/**
 * Created by fhf11991 on 2017/4/18.
 */

public class BaseViewStub extends View {

    public BaseViewStub(Context context) {
        this(context, null);
    }

    public BaseViewStub(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseViewStub(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVisibility(GONE);
        setWillNotDraw(true);
    }

    /**
     * Whether create view automatic .
     * @return
     */
    protected boolean createViewAutomatic() {
        return true;
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if(createViewAutomatic()) {
            if(mInflatedViewRef == null || mInflatedViewRef.get() == null) {
                inflate();
            }
        }
    }

    private WeakReference<View> mInflatedViewRef;

    public View inflate() {
        final ViewParent viewParent = getParent();

        if (viewParent != null && viewParent instanceof ViewGroup) {
            final ViewGroup parent = (ViewGroup) viewParent;
            final LayoutInflater factory = LayoutInflater.from(getContext());
            final View view = onCreateView(factory, parent);

            if (getId() != NO_ID) {
                view.setId(getId());
            }

            final int index = parent.indexOfChild(this);
            parent.removeViewInLayout(this);

            final ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != null) {
                parent.addView(view, index, layoutParams);
            } else {
                parent.addView(view, index);
            }

            mInflatedViewRef = new WeakReference<>(view);
            return view;
        } else {
            throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (mInflatedViewRef != null) {
            View view = mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
            } else {
                throw new IllegalStateException("setVisibility called on un-referenced view");
            }
        } else {
            super.setVisibility(visibility);
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
    }
}
