package com.aitangba.testproject.slideback.slidinglayout;

import android.view.View;
import java.util.Stack;

/**
 * user:czy
 * * Date: 2015-07-03
 * Time: 16:11
 */
public class LastActivities {
    private static LastActivities instance;
    private Stack<View> decorViews;

    public static LastActivities getInstance() {
        if (instance == null) {
            instance = new LastActivities();
        }
        return instance;
    }

    public int getCount() {
        return decorViews == null ? 0 : decorViews.size();
    }

    public View getTopView() {
        if (decorViews != null || decorViews.size() > 0) {
            return decorViews.peek();
        }
        return null;
    }

    public void removeView(View decorView) {
        if (decorViews != null) {
            decorViews.remove(decorView);
        }
    }

    public void addView(View decorView) {
        if (decorViews == null)
            decorViews = new Stack<>();
        decorViews.add(decorView);
    }
}
