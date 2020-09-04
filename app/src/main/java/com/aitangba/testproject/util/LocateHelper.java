package com.aitangba.testproject.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.List;

public class LocateHelper {

    private static final String TAG = "LocateHelper";
    private static final String TAG_INNER_FRAGMENT = "LocateFragment";
    private FragmentActivity mFragmentActivity;

    private LocateHelper(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    public static LocateHelper with(@NonNull FragmentActivity fragmentActivity) {
        return new LocateHelper(fragmentActivity);
    }

    public static boolean isLocationEnabled(@NonNull final Context context) {
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

    public void oneOffLocate(boolean useCache, @NonNull final LocateListener listener) {
        // check locate switch
        if (!isLocationEnabled(mFragmentActivity)) {
            listener.onError(AMapLocation.ERROR_CODE_SERVICE_FAIL, "locate service is stopped");
            return;
        }

        // check permission
        int state = ContextCompat.checkSelfPermission(mFragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (state == PackageManager.PERMISSION_DENIED) {
            listener.onError(AMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION, "locate permission is denied");
            return;
        }

        List<Fragment> fragmentList = mFragmentActivity.getSupportFragmentManager().getFragments();
        LocateFragment locateFragment = null;
        for (Fragment fragment : fragmentList) {
            if (fragment != null && fragment instanceof LocateFragment) {
                locateFragment = (LocateFragment) fragment;
                break;
            }
        }

        if (locateFragment == null) {
            locateFragment = (LocateFragment) Fragment.instantiate(mFragmentActivity, LocateFragment.class.getName());
            mFragmentActivity.getSupportFragmentManager().beginTransaction().add(locateFragment, TAG_INNER_FRAGMENT).commitAllowingStateLoss();
        }

        // start locate
        locateFragment.add(mFragmentActivity, useCache, listener);
    }


    public interface LocateListener {

        void onSuccess(@NonNull AMapLocation location);

        void onError(int errorCode, @NonNull String errorDesc);

    }

    public static class LocateFragment extends Fragment implements AMapLocationListener {

        private AMapLocationClient mLocationClient;
        private ArrayList<LocateListener> mLocateListeners = new ArrayList<>();

        public void add(Context context, boolean useCache, LocateListener locateListener) {
            if (useCache) { // return cached location
                if (mLocationClient == null) {
                    mLocationClient = new AMapLocationClient(context);
                    mLocationClient.setLocationOption(getDefaultOption());
                }
                AMapLocation location = mLocationClient.getLastKnownLocation();
                if (location != null && location.getLatitude() > 0 && location.getLongitude() > 0) {
                    locateListener.onSuccess(location);
                    return;
                }
            }

            mLocateListeners.add(locateListener);

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
                    for (LocateListener locateListener : mLocateListeners) {
                        locateListener.onError(-1, "信息不完整");
                    }
                    return;
                } else {
                    for (LocateListener locateListener : mLocateListeners) {
                        locateListener.onSuccess(aMapLocation);
                    }
                }
            } else {
                for (LocateListener locateListener : mLocateListeners) {
                    locateListener.onError(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
                }
            }
            mLocateListeners.clear();
            mLocationClient.stopLocation();
        }
    }
}
