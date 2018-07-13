package com.aitangba.testproject.view.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.aitangba.testproject.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fhf11991 on 2018/7/13
 */
public class IDCardInputHelper {

    private Activity mActivity;
    private EditText mEditText;
    private View mContentView;
    private boolean mShowing;

    private SparseArray<String> mSparseArray = new SparseArray<>();
    {
        mSparseArray.append(0, "0");
        mSparseArray.append(10, "1");
        mSparseArray.append(20, "2");
        mSparseArray.append(30, "3");
        mSparseArray.append(40, "4");
        mSparseArray.append(50, "5");
        mSparseArray.append(60, "6");
        mSparseArray.append(70, "7");
        mSparseArray.append(80, "8");
        mSparseArray.append(90, "9");
        mSparseArray.append(101, "X");

    }

    public IDCardInputHelper(@NonNull Activity activity, @NonNull EditText editText) {
        mActivity = activity;
        mEditText = editText;
        initEditText();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEditText() {
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    show();
                }
                return false;
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    dismiss();
                }
            }
        });

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if(currentVersion < 14) {
            mEditText.setInputType(InputType.TYPE_NULL);
            return;
        }

        String methodName = currentVersion >= 16 ? "setShowSoftInputOnFocus" : "setSoftInputShownOnFocus";
        Class<EditText> cls = EditText.class;
        Method setShowSoftInputOnFocus;
        try {
            setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(mEditText, false);
        } catch (NoSuchMethodException e) {
            mEditText.setInputType(InputType.TYPE_NULL);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void show() {
        hideSoftInput();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;

        FrameLayout rootView = mActivity.findViewById(Window.ID_ANDROID_CONTENT);
        if(mContentView == null) {
            mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_id_card_keyboard, rootView, false);
            IDCardKeyboardView cardKeyboardView = mContentView.findViewById(R.id.keyboardView);
            cardKeyboardView.setOnKeyClickListener(new IDCardKeyboardView.OnKeyClickListener() {
                @Override
                public void onKey(int primaryCode) {
                    Editable editable = mEditText.getText();
                    int start = mEditText.getSelectionStart();
                    if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                        if (editable != null && editable.length() > 0) {
                            if (start > 0) {
                                editable.delete(start - 1, start);
                            }
                        }
                    } else {
                        String str = mSparseArray.get(primaryCode);
                        if(!TextUtils.isEmpty(str)) {
                            editable.insert(start, str);
                        }
                    }
                }
            });
        } else {
            rootView.removeView(mContentView);
        }
        rootView.addView(mContentView, layoutParams);
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.bottom_in);
        mContentView.startAnimation(animation);
        mShowing = true;
    }

    private void dismiss() {
        if(mContentView == null) {
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.bottom_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mContentView != null) {
                    FrameLayout rootView = mActivity.findViewById(Window.ID_ANDROID_CONTENT);
                    rootView.removeView(mContentView);
                    mShowing = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mContentView.startAnimation(animation);
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }

    public boolean dispatchBackEvent() {
        if(mShowing) {
            dismiss();
            return true;
        }
        return false;
    }
}
