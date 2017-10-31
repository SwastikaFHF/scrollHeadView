package com.aitangba.testproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aitangba.testproject.baseui.test.LoadingTestActivity;
import com.aitangba.testproject.calendarview.CalendarActivity;
import com.aitangba.testproject.cornerrectangle.CornerRectangleActivity;
import com.aitangba.testproject.customswipe.CustomSwipeActivity;
import com.aitangba.testproject.drawable.DrawableTestActivity;
import com.aitangba.testproject.edgeeffect.EffectActivity;
import com.aitangba.testproject.flowlayout.FlowViewActivity;
import com.aitangba.testproject.fragment.FragmentTestActivity;
import com.aitangba.testproject.horizonscroll.HorizonScrollActivity;
import com.aitangba.testproject.horizonscroll.HorizonScrollTestActivity;
import com.aitangba.testproject.irregularview.IrregularViewActivity;
import com.aitangba.testproject.lightadapter.viewmodel.LightAdapterActivity;
import com.aitangba.testproject.loadingview.LoadViewActivity;
import com.aitangba.testproject.multiadapter.ui.MultiAdapterActivity;
import com.aitangba.testproject.numberpicker.NumberPickerActivity;
import com.aitangba.testproject.paging.PagingListViewActivity;
import com.aitangba.testproject.paging.PagingRecyclerViewActivity;
import com.aitangba.testproject.path.PathActivity;
import com.aitangba.testproject.progressbar.ProgressbarActivity;
import com.aitangba.testproject.removeitem.RemoveItemActivity;
import com.aitangba.testproject.runnablemanager.RunnableManagerActivity;
import com.aitangba.testproject.slideback.slidingmenu.SlidingMenuActivity;
import com.aitangba.testproject.threadpool.ThreadPoolActivity;
import com.aitangba.testproject.threadpool.volley.VolleyActivity;
import com.aitangba.testproject.ubb.UbbActivity;
import com.aitangba.testproject.verticalnestedscroll.listview.ListViewActivity;
import com.aitangba.testproject.verticalnestedscroll.nestedscrollview.NestedScrollActivity;
import com.aitangba.testproject.viewpager.ViewPageActivity;
import com.aitangba.testproject.wheelview.WheelViewActivity;
import com.aitangba.testproject.youtube.YoutubeActivity;

import java.util.ArrayList;

/**
 * Created by fhf11991 on 2016/6/22.
 */
public class MainActivity extends AppCompatActivity {

    private ArrayList<ActivityInfo> activityInfoList = new ArrayList<>();
    {
        activityInfoList.add(new ActivityInfo("选择数字", NumberPickerActivity.class));
        activityInfoList.add(new ActivityInfo("水平滚动", HorizonScrollActivity.class));
        activityInfoList.add(new ActivityInfo("垂直滚动", ListViewActivity.class));
        activityInfoList.add(new ActivityInfo("自定义Swipe", CustomSwipeActivity.class));
        activityInfoList.add(new ActivityInfo("水平滑动", HorizonScrollTestActivity.class));
        activityInfoList.add(new ActivityInfo("自定义多样式adapter", MultiAdapterActivity.class));
        activityInfoList.add(new ActivityInfo("自定义多样式view_page", ViewPageActivity.class));
        activityInfoList.add(new ActivityInfo("自定义圆角背景的TextView", CornerRectangleActivity.class));
        activityInfoList.add(new ActivityInfo("可滑动的activity", SlidingMenuActivity.class));
        activityInfoList.add(new ActivityInfo("水平滑动广告图", com.aitangba.testproject.horizonscrollview.HorizonScrollActivity.class));
        activityInfoList.add(new ActivityInfo("YouTube", YoutubeActivity.class));
        activityInfoList.add(new ActivityInfo("边缘效应", EffectActivity.class));
        activityInfoList.add(new ActivityInfo("自定义progressbar", ProgressbarActivity.class));
        activityInfoList.add(new ActivityInfo("自定义Path", PathActivity.class));
        activityInfoList.add(new ActivityInfo("WheelView", WheelViewActivity.class));
        activityInfoList.add(new ActivityInfo("嵌套滑动ViewGroup", NestedScrollActivity.class));
        activityInfoList.add(new ActivityInfo("lightAdapter", LightAdapterActivity.class));
        activityInfoList.add(new ActivityInfo("自定义加载View", LoadViewActivity.class));
        activityInfoList.add(new ActivityInfo("ubb转换", UbbActivity.class));
        activityInfoList.add(new ActivityInfo("登录测试", com.aitangba.testproject.login.MainActivity.class));
        activityInfoList.add(new ActivityInfo("删除动画测试", RemoveItemActivity.class));
        activityInfoList.add(new ActivityInfo("不规则图形测试", IrregularViewActivity.class));

        activityInfoList.add(new ActivityInfo("登录测试", LoadingTestActivity.class));
        activityInfoList.add(new ActivityInfo("分页", PagingListViewActivity.class));
        activityInfoList.add(new ActivityInfo("ThreadPoolActivity", ThreadPoolActivity.class));
        activityInfoList.add(new ActivityInfo("PagingRecyclerViewActivity", PagingRecyclerViewActivity.class));
        activityInfoList.add(new ActivityInfo("浮动View", FlowViewActivity.class));
        activityInfoList.add(new ActivityInfo("Drawable", DrawableTestActivity.class));
        activityInfoList.add(new ActivityInfo("RunnableManager", RunnableManagerActivity.class));
        activityInfoList.add(new ActivityInfo("FragmentTestActivity", FragmentTestActivity.class));
        activityInfoList.add(new ActivityInfo("VolleyActivity", VolleyActivity.class));
        activityInfoList.add(new ActivityInfo("CalendarActivity", CalendarActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.testBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onClick();
            }
        });
    }

    private void onClick() {
        new AlertDialog.Builder(this).setSingleChoiceItems(getTitles(), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                startActivity(activityInfoList.get(which).activityClass);
            }
        }).show();
    }

    private String[] getTitles() {
        String[] titles = new String[activityInfoList.size()];

        for(int i = 0, size = activityInfoList.size(); i < size ; i++) {
            titles[i] = activityInfoList.get(i).name;
        }

        return titles;
    }

    private void startActivity(Class<?> activityClass) {
        if(activityClass != null) {
            startActivity(new Intent(this, activityClass));
        }
    }

    private static class ActivityInfo {
        public String name;
        public Class<?> activityClass;

        public ActivityInfo(String name, Class<?> activityClass) {
            this.name = name;
            this.activityClass = activityClass;
        }
    }
}
