package com.aitangba.testproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Create by XBeats on 2018/11/30
 */
public class UserWelfareTextView extends AppCompatTextView {

    private float percentage = 0.0095f;

    private Paint mBgPaint = new Paint();
    private Path mBgPath = new Path();

    private Paint mFramePaint = new Paint();
    private Path mFramePath = new Path();
    private static final float S_FRAME_WIDTH = 0.5F; // dp
    private int mFrameWidth; // px

    public UserWelfareTextView(Context context) {
        super(context);
        init();
    }

    public UserWelfareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserWelfareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mFrameWidth = dp2px(getContext(), S_FRAME_WIDTH);

        mBgPaint.setColor(Color.parseColor("#DBE8FF"));
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);

        mFramePaint.setColor(Color.parseColor("#A5C6FF"));
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setAntiAlias(true);
        mFramePaint.setStrokeWidth(dp2px(getContext(), mFrameWidth));
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int actW = w - mFrameWidth * 2;
        int actH = h - mFrameWidth * 2;

        mFramePath.reset();
        mFramePath.moveTo(actH / 2, actH);
        mFramePath.arcTo(new RectF(0, 0, actH, actH), 90, 180);
        mFramePath.arcTo(new RectF(actW - actH, 0, actW, actH), 270, 180);
        mFramePath.close();
    }

    private RectF mBgRectF = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        int count = canvas.save();
        canvas.translate(mFrameWidth, mFrameWidth);

        mBgPath.reset();
        if (percentage == 1) {
            canvas.drawPath(mFramePath, mBgPaint);
        } else if (percentage != 0) {
            int h = getMeasuredHeight() - mFrameWidth * 2;
            int w = getMeasuredWidth() - mFrameWidth * 2;
            mBgRectF.left = 0;
            mBgRectF.top = 0;
            mBgRectF.right = h;
            mBgRectF.bottom = h;
            mBgPath.arcTo(mBgRectF, 90, 180);
            mBgPath.lineTo(h / 2 + (w - h) * percentage, 0);
            mBgPath.lineTo(h / 2 + (w - h) * percentage, h);
            mBgPath.close();
            canvas.drawPath(mBgPath, mBgPaint);
        }

        canvas.drawPath(mFramePath, mFramePaint);

        canvas.restoreToCount(count);
        super.onDraw(canvas);
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
