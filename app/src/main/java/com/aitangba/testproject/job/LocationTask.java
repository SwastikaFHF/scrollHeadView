package com.aitangba.testproject.job;

import android.app.Activity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by fhf11991 on 2018/2/23.
 */

public abstract class LocationTask extends SyncJob implements AMapLocationListener {
    private Activity mActivity;

    //声明mLocationClient对象
    private AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption;

    public LocationTask(Activity activity) {
        this.mActivity = activity;

        mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);

        mLocationClient = new AMapLocationClient(mActivity);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                mLocationClient.stopLocation();
                LocationTask.this.onLocationChanged(aMapLocation);
            }
        });
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void execute() {
        mLocationClient.startLocation();
    }

    @Override
    public void cancel() {
        super.cancel();
        mLocationClient.stopLocation();
        mLocationClient = null;
        mLocationOption = null;
    }

    public abstract void onLocationChanged(AMapLocation aMapLocation);
}
