package com.aitangba.testproject.invisiblefragment.amap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.aitangba.testproject.invisiblefragment.ViewUtils;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AMapTask {

    private static final String TAG = "LocateHelper";
    private static final String TAG_INNER_FRAGMENT = "LocateFragment";
    private final Context mContext;
    private final FragmentManager mFragmentManager;

    public AMapTask(FragmentActivity fragmentActivity) {
        this.mContext = fragmentActivity;
        this.mFragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    public AMapTask(Fragment fragment) {
        this.mContext = fragment.getContext();
        this.mFragmentManager = fragment.getChildFragmentManager();
    }

    public AMapTask(View view) {
        this.mContext = view.getContext();
        this.mFragmentManager = ViewUtils.findFragmentManager(view);
    }

    public final void start(boolean useCache) {
        // check locate switch
        if (!isLocationEnabled(mContext)) {
            onError(AMapLocation.ERROR_CODE_SERVICE_FAIL, "locate service is stopped");
            return;
        }

        // check permission
        int state = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (state == PackageManager.PERMISSION_DENIED) {
            onError(AMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION, "locate permission is denied");
            return;
        }

        List<Fragment> fragmentList = mFragmentManager.getFragments();
        LocateFragment locateFragment = null;
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof LocateFragment) {
                locateFragment = (LocateFragment) fragment;
                break;
            }
        }

        if (locateFragment == null) {
            locateFragment = (LocateFragment) Fragment.instantiate(mContext, LocateFragment.class.getName());
            mFragmentManager.beginTransaction().add(locateFragment, TAG_INNER_FRAGMENT).commitAllowingStateLoss();
        }

        // start locate
        locateFragment.add(mContext, this, useCache);
    }

    protected void onSuccess(@NonNull AMapLocation location){

    }

    protected void onError(int errorCode, @NonNull String errorDesc){

    }

    private boolean isLocationEnabled(@NonNull final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }

        try {
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gps || network;
        } catch (Exception e) {
            return false;
        }
    }

    public static class LocateFragment extends Fragment implements AMapLocationListener {

        private AMapLocationClient mLocationClient;
        private ArrayList<AMapTask> mLocateListeners = new ArrayList<>();

        public void add(Context context, AMapTask aMapTask, boolean useCache) {
            if (useCache) { // return cached location
                if (mLocationClient == null) {
                    mLocationClient = new AMapLocationClient(context);
                    mLocationClient.setLocationOption(getDefaultOption());
                }
                AMapLocation location = mLocationClient.getLastKnownLocation();
                if (location != null && location.getLatitude() > 0 && location.getLongitude() > 0) {
                    aMapTask.onSuccess(location);
                    return;
                }
            }

            mLocateListeners.add(aMapTask);

            if (mLocationClient == null) {
                mLocationClient = new AMapLocationClient(context);
                mLocationClient.setLocationOption(getDefaultOption());
                mLocationClient.setLocationListener(this);
                mLocationClient.startLocation();
            } else {
                mLocationClient.startLocation();
            }
        }

        private AMapLocationClientOption getDefaultOption() {
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setHttpTimeOut(10000);
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
            option.setNeedAddress(true);
            option.setMockEnable(true);
            return option;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mLocationClient != null) {
                mLocationClient.onDestroy();
            }
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.d(TAG, "onLocationChanged -- "
                    + "  size = " + mLocateListeners.size()
                    + " aMapLocation = " + aMapLocation.toStr()
            );
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {

                // validate locate info
                if (aMapLocation.getLatitude() == 0 || aMapLocation.getLongitude() == 0) {
                    for (AMapTask aMapTask : mLocateListeners) {
                        aMapTask.onError(-1, "信息不完整");
                    }
                    return;
                } else {
                    for (AMapTask aMapTask : mLocateListeners) {
                        aMapTask.onSuccess(aMapLocation);
                    }
                }
            } else {
                for (AMapTask aMapTask : mLocateListeners) {
                    aMapTask.onError(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
                }
            }
            mLocateListeners.clear();
            mLocationClient.stopLocation();
        }
    }
}
