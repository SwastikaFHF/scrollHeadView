    package com.aitangba.testproject.view.slideback.slidinglayout;

    import android.content.Context;
    import android.util.AttributeSet;
    import android.util.TypedValue;
    import android.view.MotionEvent;
    import android.widget.LinearLayout;

    import com.aitangba.testproject.R;

    /**
     * Created by nahuo16 on 2015/6/18.
     */
    public class TouchLinearLayout extends LinearLayout {

        private final int barHeight;
        private OnTouchListener touchListener ;
        private boolean shouldListener ;
        private int side;
        private int downX, downY;
        private int canBackSide ;
        public TouchLinearLayout(Context context) {
            this(context, null);
        }
        public TouchLinearLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        public TouchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            side = dip2px(context, 80) ;
            canBackSide = dip2px(context, 20) ;
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.actionBarSize, value, true);
            barHeight = context.getResources().getDimensionPixelSize(value.resourceId);
        }

        private int dip2px(Context context, int dp) {
            return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX = (int) ev.getX();
                    downY = (int) ev.getY();
                    shouldListener = downX < side && downY > barHeight ;
                    break ;
                case MotionEvent.ACTION_MOVE:
                    if(shouldListener){
                        int x = (int) (ev.getX() - downX);
                        if( x> canBackSide && Math.abs(x / (ev.getY() - downY))>1.1f){
                            return true ;
                        }
                    }
                    break ;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:

                    break ;

            }
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(touchListener!=null)
                touchListener.onTouch(this , event) ;
            return true;
        }

        public void setOnTouchListener(OnTouchListener l){
            touchListener = l ;
        }
    }
