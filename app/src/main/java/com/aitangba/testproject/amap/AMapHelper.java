package com.aitangba.testproject.amap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/3/27.
 *
 * If you use cache location information,just call the startLocation,
 * You must implements those method onRequestPermissionsResult and destroyLocation.
 *
 */
public class AMapHelper {

    //申请权限后的返回码
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1001;
    private static final String PREFS_FILE_NAME = "aMap_location";
    private static final String LOCATION_LONGITUDE = "aMap_location_longitude";
    private static final String LOCATION_LATITUDE = "aMap_location_latitude";
    private static final String LOCATION_PROVINCE = "aMap_location_province";
    private static final String LOCATION_CITY = "aMap_location_city";
    private static final String LOCATION_DISTRICT = "aMap_location_district";
    private static final String LOCATION_ADDRESS = "aMap_location_address";

    //还需申请的权限列表
    private List<String> permissionsList = new ArrayList<>();

    private AMapLocationClient locationClient;
    private Activity mActivity;
    private Fragment mFragment;
    private Callback mCallback;

    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(mCallback == null) return;

            if(aMapLocation == null) {
                mCallback.onFail("");
            } else if(aMapLocation.getErrorCode() == 0) {
                saveAMapLocation(aMapLocation);
                mCallback.onSuccess(aMapLocation);
            } else {
                mCallback.onFail("错误信息:" + aMapLocation.getErrorInfo());
            }
        }
    };

    public AMapHelper(Activity activity) {
        mActivity = activity;
        init(mActivity);
    }

    public AMapHelper(Fragment fragment) {
        mFragment = fragment;
        init(fragment.getActivity());
    }

    private void init(Context context) {
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
        // 设置是否单次定位
        locationOption.setOnceLocation(true);
        locationOption.setHttpTimeOut(2000);

        locationClient = new AMapLocationClient(context.getApplicationContext());
        locationClient.setLocationListener(locationListener);
        locationClient.setLocationOption(locationOption);
    }

    private Activity getActivity() {
        Activity activity = mActivity;
        if(activity == null) {
            activity = mFragment.getActivity();
        }
        return activity;
    }

    public void startLocation(Callback callback) {
        startLocation(true, callback);
    }

    public void startLocation(boolean useLocalCache, Callback callback) {
        if(useLocalCache) {
            AMapLocation aMapLocation = getCachedAMapLocation();
            if(aMapLocation != null && callback != null) {
                callback.onSuccess(aMapLocation);
                return;
            }
        }

        mCallback = callback;

        if(Build.VERSION.SDK_INT >= 23) {
            checkRequiredPermission();
        } else {
            locationClient.startLocation();
        }
    }

    private AMapLocation getCachedAMapLocation() {
        Activity activity = getActivity();
        if(activity == null) return null;

        final SharedPreferences preferences = activity.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        String province = preferences.getString(LOCATION_PROVINCE, "");
        if(TextUtils.isEmpty(province)) {
           return null;
        }

        double latitude = Double.longBitsToDouble(preferences.getLong(LOCATION_LATITUDE, 0));
        double longitude = Double.longBitsToDouble(preferences.getLong(LOCATION_LONGITUDE, 0));
        String city = preferences.getString(LOCATION_CITY, "");
        String district = preferences.getString(LOCATION_DISTRICT, "");
        String address = preferences.getString(LOCATION_ADDRESS, "");

        AMapLocation location = new AMapLocation("cache");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setProvince(province);
        location.setCity(city);
        location.setDistrict(district);
        location.setAddress(address);
        return location;
    }

    private void saveAMapLocation(AMapLocation aMapLocation) {
        Activity activity = getActivity();
        if(activity == null) return;

        SharedPreferences preferences = activity.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LOCATION_LATITUDE, Double.doubleToRawLongBits(aMapLocation.getLatitude()));
        editor.putLong(LOCATION_LONGITUDE, Double.doubleToRawLongBits(aMapLocation.getLongitude()));
        editor.putString(LOCATION_PROVINCE, aMapLocation.getProvince());
        editor.putString(LOCATION_CITY, aMapLocation.getCity());
        editor.putString(LOCATION_DISTRICT, aMapLocation.getDistrict());
        editor.putString(LOCATION_ADDRESS, aMapLocation.getAddress());
        editor.commit();
    }

    //所需要申请的权限数组
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private void checkRequiredPermission(){
        Activity activity = mActivity;
        if(activity == null) {
            activity = mFragment.getActivity();
        }

        if(activity == null) {
            return;
        }

        permissionsList.clear();
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }

        if(permissionsList.size() == 0) {
            locationClient.startLocation();
        } else {
            if(mActivity != null) {
                ActivityCompat.requestPermissions(mActivity, permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
            } else if(mFragment != null) {
                mFragment.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                boolean allPermissionsAllowed = true;
                for (int i=0; i<permissions.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsAllowed = false;
                    }
                }

                if(allPermissionsAllowed) {
                    locationClient.startLocation();
                } else {
                    if(mCallback != null) {
                        mCallback.onFail("错误信息: 权限不足");
                    }
                }
                break;
        }
    }

    /**
     * 如果AMapLocationClient是在当前Activity实例化的，
     * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
     */
    public void destroyLocation(){
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    public static String getLocationStr(AMapLocation location){
        StringBuffer sb = new StringBuffer();
        sb.append("定位成功" + "\n");
        sb.append("经    度    : " + location.getLongitude() + "\n");
        sb.append("纬    度    : " + location.getLatitude() + "\n");
        sb.append("省            : " + location.getProvince() + "\n");
        sb.append("市            : " + location.getCity() + "\n");
        sb.append("区            : " + location.getDistrict() + "\n");
        sb.append("地    址    : " + location.getAddress() + "\n");
        return sb.toString();
    }

    public interface Callback {

        void onSuccess(AMapLocation aMapLocation);

        void onFail(String errorInfo);
    }
}
