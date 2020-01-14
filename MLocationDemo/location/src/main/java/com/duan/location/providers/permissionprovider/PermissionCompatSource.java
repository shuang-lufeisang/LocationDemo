package com.duan.location.providers.permissionprovider;

import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/** 权限兼容 */
public class PermissionCompatSource {

    // 是否显示请求权限的理由
    boolean shouldShowRequestPermissionRationale(Fragment fragment, String permission) {
        return fragment.shouldShowRequestPermissionRationale(permission);
    }
    // 请求要授予此app 的权限
    void requestPermissions(Fragment fragment, String[] requiredPermissions, int requestCode) {
        fragment.requestPermissions(requiredPermissions, requestCode);
    }

    // 是否显示请求权限的理由
    boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    // 请求要授予此app 的权限
    void requestPermissions(Activity activity, String[] requiredPermissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, requiredPermissions, requestCode);
    }

}
