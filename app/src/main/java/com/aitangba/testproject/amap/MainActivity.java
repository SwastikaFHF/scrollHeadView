package com.aitangba.testproject.amap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;

public class MainActivity extends AppCompatActivity {

    private AMapHelper aMapHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        aMapHelper.startLocation(new AMapHelper.Callback() {
            @Override
            public void onSuccess(AMapLocation aMapLocation) {
                AMapHelper.getLocationStr(aMapLocation);
            }

            @Override
            public void onFail(String errorInfo) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aMapHelper.destroyLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        aMapHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
