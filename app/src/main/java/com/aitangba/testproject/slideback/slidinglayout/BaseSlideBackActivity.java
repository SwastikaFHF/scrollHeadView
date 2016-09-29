package com.aitangba.testproject.slideback.slidinglayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nahuo16 on 2015/6/19.
 */
public class BaseSlideBackActivity extends BaseActivity {
    private VelocityTracker velocity;
    private int width ;
    int rightMargin ;
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View decorView = getWindow().getDecorView() ;
        decorView.setBackgroundColor(Color.TRANSPARENT);
        ((ViewGroup)decorView).getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        width = getResources().getDisplayMetrics().widthPixels ;
        rightMargin = dip2px(getApplicationContext(), 100);
    }

    public void setSlideBack(TouchLinearLayout layout){
        layout.setOnTouchListener(touchListener);
    }

    void doTouchEvent(MotionEvent event){
        int x = (int) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(velocity == null){
                    velocity = VelocityTracker.obtain() ;
                }
                else{
                    velocity.clear();
                }
                velocity.addMovement(event);
                break ;
            case MotionEvent.ACTION_MOVE:
                if(x > 0){
                    setX(x) ;
                }
                if(velocity == null){
                    velocity = VelocityTracker.obtain() ;
                }
                else{
                    velocity.clear();
                }
                velocity.addMovement(event);
                velocity.computeCurrentVelocity(1000);
                break ;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(x > 0){
                    setX(x);
                }
                int plus = dip2px(getApplicationContext() , 20) ;
                int add = -plus ;
                int max = (int) event.getX();

                if(max>width/3 || (velocity!=null&&velocity.getXVelocity()>2000)){
                    add = plus ;
                }
                final int a = add ;
                final int m = max ;
                new Thread(){
                    public void run(){
                        int run = m ;
                        int add = a ;
                        int addPlus = add / 10 ;
                        while(run > 0 && run < width ){
                            try {
                                Thread.sleep(10) ;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            run += add ;
                            add += addPlus ;
                            final int r = run ;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   setX(r) ;
                                }
                            });
                        }
                        if(run <= 0){
                            final int r = 0 ;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setX(r) ;
                                }
                            });
                        }
                        else{
                            final int r = width ;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setX(r) ;
                                    finish();
                                }
                            });
                        }
                    }
                }.start();
                break ;
        }
    }

    private void setX(int x) {
        getWindow().getDecorView().setX(x);
        View decorView = LastActivities.getInstance().getTopView() ;
        if(decorView != null  ){
            decorView.setX(-width/3 + x/3) ;
        }
    }

    private View.OnTouchListener touchListener =new View.OnTouchListener() {
        VelocityTracker velocity ;
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
           doTouchEvent(event);
            return false;
        }
    } ;

    private int dip2px(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
