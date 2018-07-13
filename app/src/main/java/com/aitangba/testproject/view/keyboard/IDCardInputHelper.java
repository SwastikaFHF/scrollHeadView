package com.aitangba.testproject.view.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.aitangba.testproject.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fhf11991 on 2018/7/13
 */
public class IDCardInputHelper {

    private Activity mActivity;
    private EditText mEditText;
    private View mContentView;
    private InnerHandler mInnerHandler;
    private int mContentViewHeight;
    private boolean mShowing = false;

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
        mInnerHandler = new InnerHandler(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEditText() {
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && !mShowing) {
                    show();
                }
                return false;
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mContentView != null) {
                    dismiss();
                }
            }
        });

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion < 14) {
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
        mShowing = true;

        FrameLayout rootView = mActivity.findViewById(Window.ID_ANDROID_CONTENT);
        if (mContentView == null) {
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
                        if (!TextUtils.isEmpty(str)) {
                            editable.insert(start, str);
                        }
                    }
                }
            });
            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mContentView.measure(spec, spec);
            mContentViewHeight = mContentView.getMeasuredHeight();
        } else {
            rootView.removeView(mContentView);
        }
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            mInnerHandler.startLatter();
        } else {
            mInnerHandler.startNow();
        }
    }

    private void startPlay() {
        FrameLayout rootView = mActivity.findViewById(Window.ID_ANDROID_CONTENT);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        rootView.addView(mContentView, layoutParams);

        View scrollToView = ((ViewGroup) mActivity.findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);
        int[] location = new int[2];
        mEditText.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标

        int originY = location[1];
        DisplayMetrics display = mActivity.getResources().getDisplayMetrics();
        int limitHeight = display.heightPixels - mContentViewHeight;

        Interpolator interpolator = new DecelerateInterpolator(2f);

        ObjectAnimator keyboardYAnimator = new ObjectAnimator();
        keyboardYAnimator.setInterpolator(interpolator);
        keyboardYAnimator.setProperty(View.TRANSLATION_Y);
        keyboardYAnimator.setFloatValues(mContentViewHeight, 0);
        keyboardYAnimator.setTarget(mContentView);

        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet.Builder builder = animatorSet.play(keyboardYAnimator);
        if (originY > limitHeight) {
            ObjectAnimator targetViewYAnimator = new ObjectAnimator();
            targetViewYAnimator.setInterpolator(interpolator);
            targetViewYAnimator.setProperty(View.TRANSLATION_Y);
            targetViewYAnimator.setFloatValues(0, limitHeight - originY - mEditText.getMeasuredHeight() - 10);
            targetViewYAnimator.setTarget(scrollToView);
            builder.with(targetViewYAnimator);
        }
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    private void dismiss() {
        View scrollToView = ((ViewGroup) mActivity.findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);

        Interpolator interpolator = new DecelerateInterpolator(2f);

        ObjectAnimator keyboardYAnimator = new ObjectAnimator();
        keyboardYAnimator.setInterpolator(interpolator);
        keyboardYAnimator.setProperty(View.TRANSLATION_Y);
        keyboardYAnimator.setFloatValues(0, mContentViewHeight);
        keyboardYAnimator.setTarget(mContentView);

        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet.Builder builder = animatorSet.play(keyboardYAnimator);
        if (scrollToView.getTranslationY() != 0) {
            ObjectAnimator targetViewYAnimator = new ObjectAnimator();
            targetViewYAnimator.setInterpolator(interpolator);
            targetViewYAnimator.setProperty(View.TRANSLATION_Y);
            targetViewYAnimator.setFloatValues(scrollToView.getTranslationY(), 0);
            targetViewYAnimator.setTarget(scrollToView);
            builder.with(targetViewYAnimator);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShowing = false;
            }
        });
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    public boolean dispatchBackEvent() {
        if (mShowing) {
            dismiss();
            mInnerHandler.removeCallbacksAndMessages(null);
            return true;
        }
        return false;
    }

    private static final int MSG_PLAY_NOW = 1;
    private static final int MSG_PLAY_LATTER = 2;
    private static class InnerHandler extends Handler {
        private WeakReference<IDCardInputHelper> mWeakReference;

        private InnerHandler(IDCardInputHelper idCardInputHelper) {
            mWeakReference = new WeakReference<>(idCardInputHelper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_PLAY_NOW || msg.what == MSG_PLAY_LATTER) {
                IDCardInputHelper idCardInputHelper = mWeakReference.get();
                if(idCardInputHelper != null) {
                    idCardInputHelper.startPlay();
                }
            }
        }

        private void startNow() {
            Message message = obtainMessage();
            message.what = MSG_PLAY_NOW;
            message.sendToTarget();
        }

        private void startLatter() {
            sendEmptyMessageDelayed(MSG_PLAY_LATTER, 250);
        }
    }
}
