package com.aitangba.testproject.view.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/7/12
 */
public class IDCardKeyboardView extends KeyboardView {

    private static final float TEXT_SIZE = 27f;
    private Rect mBackgroundBounds = new Rect();
    private Paint mBackgroundPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Rect mKeyTextBounds = new Rect();
    private Rect mKeyBackgroundBounds = new Rect();
    private Paint mKeyBackgroundPaint = new Paint();

    private int mPressedColor = Color.parseColor("#ffededed");
    private int mSpecialKeyBgColor = Color.parseColor("#e4e8ec");

    public IDCardKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IDCardKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.parseColor("#cccccc"));

        mKeyBackgroundPaint.setAntiAlias(true);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#333333"));
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        setKeyboard(new Keyboard(context, R.xml.number));
        setPreviewEnabled(false);
        setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                if (mOnKeyClickListener != null) {
                    mOnKeyClickListener.onKey(primaryCode);
                }
            }

            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackgroundBounds.left = 0;
        mBackgroundBounds.top = 0;
        mBackgroundBounds.right = getMeasuredWidth();
        mBackgroundBounds.bottom = getMeasuredHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw background, to cover the origin keyboard
        canvas.drawRect(mBackgroundBounds, mBackgroundPaint);

        // draw the keys
        Keyboard keyboard = getKeyboard();
        for (Keyboard.Key key : keyboard.getKeys()) {
            drawText(canvas, key);
        }
    }

    private void drawText(Canvas canvas, Keyboard.Key key) {
        mKeyBackgroundBounds.top = key.y;
        mKeyBackgroundBounds.bottom = key.y + key.height;
        mKeyBackgroundBounds.left = key.x;
        mKeyBackgroundBounds.right = key.x + key.width;
        if (key.pressed) {
            mKeyBackgroundPaint.setColor(mPressedColor);
        } else {
            mKeyBackgroundPaint.setColor(key.codes[0] == 101 || key.codes[0] == Keyboard.KEYCODE_DELETE ? mSpecialKeyBgColor : Color.WHITE);
        }
        // draw the background of the key
        canvas.drawRect(mKeyBackgroundBounds, mKeyBackgroundPaint);

        if (key.label != null) {
            mTextPaint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), mKeyTextBounds);
            canvas.drawText(key.label.toString(), key.x + (key.width / 2), (key.y + key.height / 2) + mKeyTextBounds.height() / 2, mTextPaint);
        } else if (key.icon != null) {
            Drawable drawable = key.icon;
            int top = key.y + (key.height - drawable.getIntrinsicHeight()) / 2;
            int left = key.x + (key.width - drawable.getIntrinsicWidth()) / 2;
            drawable.setBounds(left, top, left + drawable.getIntrinsicWidth(), top + drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }
    }

    private OnKeyClickListener mOnKeyClickListener;

    public void setOnKeyClickListener(OnKeyClickListener onKeyClickListener) {
        mOnKeyClickListener = onKeyClickListener;
    }

    public interface OnKeyClickListener {
        void onKey(int primaryCode);
    }
}
