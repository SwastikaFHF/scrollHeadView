package com.aitangba.testproject.invisiblefragment.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.aitangba.testproject.invisiblefragment.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissionTask {
    private static final String TAG_INNER_FRAGMENT = "PermissionFragment";
    private final Context mContext;
    private final FragmentManager mFragmentManager;

    public PermissionTask(FragmentActivity fragmentActivity) {
        this.mContext = fragmentActivity;
        this.mFragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    public PermissionTask(Fragment fragment) {
        this.mContext = fragment.getContext();
        this.mFragmentManager = fragment.getChildFragmentManager();
    }

    public PermissionTask(View view) {
        this.mContext = view.getContext();
        this.mFragmentManager = ViewUtils.findFragmentManager(view);
    }

    public void start(@NonNull String... permissions) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                boolean granted = ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    grantedList.add(permission);
                } else {
                    deniedList.add(permission);
                }
            }

            if (grantedList.size() == permissions.length) {
                onSuccess();
            } else if (deniedList.size() > 0) {
                List<Fragment> fragmentList = mFragmentManager.getFragments();
                PermissionFragment permissionFragment = null;
                for (Fragment fragment : fragmentList) {
                    if (fragment != null && fragment instanceof PermissionFragment) {
                        permissionFragment = (PermissionFragment) fragment;
                        break;
                    }
                }

                if (permissionFragment == null) {
                    permissionFragment = (PermissionFragment) Fragment.instantiate(mContext, PermissionFragment.class.getName());
                    mFragmentManager.beginTransaction().add(permissionFragment, TAG_INNER_FRAGMENT).commitAllowingStateLoss();
                }
                permissionFragment.add(this, deniedList);
            }
        } else {
            onSuccess();
        }
    }

    protected void onSuccess() {}

    protected void onFailed(){}

    public static class PermissionFragment extends Fragment {
        private static final int INIT_CODE = -1;

        private int mReqCode = INIT_CODE;
        private PermissionTask mPermissionTask;

        public void add(PermissionTask permissionTask, List<String> deniedList) {
            mPermissionTask = permissionTask;
            mReqCode = (mReqCode + 1) % 100;

            requestPermissions(deniedList.toArray(new String[deniedList.size()]), mReqCode);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (mReqCode == INIT_CODE || mReqCode != requestCode) {
                return;
            }

            List<String> grantedList = new ArrayList<>();
            List<String> deniedList = new ArrayList<>();

            for (int i = 0; i < grantResults.length; i++) {
                boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    grantedList.add(permissions[i]);
                } else {
                    deniedList.add(permissions[i]);
                }
            }

            if (deniedList.size() > 0) {
                mPermissionTask.onFailed();
            } else if (grantedList.size() > 0) {
                mPermissionTask.onSuccess();
            }

            mPermissionTask.mFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }
}

