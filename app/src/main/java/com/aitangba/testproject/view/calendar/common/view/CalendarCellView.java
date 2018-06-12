package com.aitangba.testproject.view.calendar.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.aitangba.testproject.R;
import com.aitangba.testproject.view.calendar.common.pojo.CellBean;


/**
 * Created by fhf11991 on 2018/6/11
 */
public class CalendarCellView extends RelativeLayout {

    private static final int[] STATE_WEEKEND = {R.attr.state_weekend};
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean mIsWeekend = false; // 是否周末
    private boolean mChecked = false; // 是否可选中
    private int option = CellBean.OPTION_NONE; //当前cell具体状态


    private Paint mTransparentCoverPaint;
    private Paint mSelectCoverPaint;
    private Paint mSidesCoverPaint;

    private Path mLeftTopCoverPath = new Path();
    private Path mLeftBottomCoverPath = new Path();
    private Path mRightTopCoverPath = new Path();
    private Path mRightBottomCoverPath = new Path();
    public final static int CORNER_RADIUS = 40; // px

    public CalendarCellView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSidesCoverPaint = new Paint();
        mSidesCoverPaint.setAntiAlias(true);
        mSidesCoverPaint.setColor(Color.parseColor("#448AFF"));

        mTransparentCoverPaint = new Paint();
        mTransparentCoverPaint.setAntiAlias(true);
        mTransparentCoverPaint.setColor(Color.parseColor("#ffffff"));

        mSelectCoverPaint = new Paint();
        mSelectCoverPaint.setAntiAlias(true);
        mSelectCoverPaint.setColor(Color.parseColor("#bcddfd"));
    }

    public void setWeekend(boolean weekend) {
        this.mIsWeekend = weekend;
        refreshDrawableState();
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    public void setOption(int option) {
        this.option = option;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (!isEnabled()) {
            return drawableState;
        }

        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }

        if (mIsWeekend) {
            mergeDrawableStates(drawableState, STATE_WEEKEND);
        }
        return drawableState;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mLeftTopCoverPath.moveTo(0, 0);
        mLeftTopCoverPath.arcTo(new RectF(0, 0, CORNER_RADIUS * 2, CORNER_RADIUS * 2), 180, 90, false);

        mLeftBottomCoverPath.lineTo(0, h);
        mLeftBottomCoverPath.arcTo(new RectF(0, h - CORNER_RADIUS * 2, CORNER_RADIUS * 2, h), 90, 90, false);

        mRightTopCoverPath.moveTo(w, 0);
        mRightTopCoverPath.arcTo(new RectF(w - CORNER_RADIUS * 2, 0, w, CORNER_RADIUS * 2), 270, 90, false);

        mRightBottomCoverPath.moveTo(w, h);
        mRightBottomCoverPath.arcTo(new RectF(w - CORNER_RADIUS * 2, h - CORNER_RADIUS * 2, w, h), 0, 90, false);

    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if(option == CellBean.OPTION_FIRST) {
            canvas.drawPaint(mSidesCoverPaint);
            canvas.drawPath(mLeftTopCoverPath, mTransparentCoverPaint);
            canvas.drawPath(mLeftBottomCoverPath, mTransparentCoverPaint);
            canvas.drawPath(mRightTopCoverPath, mSelectCoverPaint);
            canvas.drawPath(mRightBottomCoverPath, mSelectCoverPaint);
        } else if(option == CellBean.OPTION_MIDDLE) {
            canvas.drawPaint(mSelectCoverPaint);
        } else if(option == CellBean.OPTION_LAST) {
            canvas.drawPaint(mSidesCoverPaint);
            canvas.drawPath(mLeftTopCoverPath, mSelectCoverPaint);
            canvas.drawPath(mLeftBottomCoverPath, mSelectCoverPaint);
            canvas.drawPath(mRightTopCoverPath, mTransparentCoverPaint);
            canvas.drawPath(mRightBottomCoverPath, mTransparentCoverPaint);
        }

        super.dispatchDraw(canvas);
    }
}
