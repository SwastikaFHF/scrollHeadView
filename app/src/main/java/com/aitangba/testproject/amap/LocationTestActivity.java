package com.aitangba.testproject.amap;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;

public class LocationTestActivity extends AppCompatActivity {

    private AMapHelper aMapHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        aMapHelper = new AMapHelper(this);

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
