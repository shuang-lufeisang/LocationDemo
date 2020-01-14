package com.duan.location.providers.permissionprovider;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.duan.location.ContextProcessor;
import com.duan.location.LocationManager;
import com.duan.location.listener.PermissionListener;
import com.duan.location.providers.dialogprovider.DialogProvider;
import com.duan.location.utils.LogUtils;

import java.lang.ref.WeakReference;

/**
 * 权限提供者-负责获取所需权限 并通知 LocationManager
 */
public abstract class PermissionProvider {

    private WeakReference<ContextProcessor> weakContextProcessor;      // 弱引用-Context处理
    private WeakReference<PermissionListener> weakPermissionListener;  // 弱引用-权限监听
    private final String[] requiredPermissions;         // 所需权限
    private DialogProvider rationalDialogProvider;      // 基本Dialog

    /**
     * This class is responsible to get required permissions, and notify {@linkplain LocationManager}.
     *
     * @param requiredPermissions are required, setting this field empty will {@throws IllegalStateException}
     * @param rationaleDialogProvider will be used to display rationale dialog when it is necessary. If this field is set
     * to null, then rationale dialog will not be displayed to user at all.
     */
    public PermissionProvider(String[] requiredPermissions, @Nullable DialogProvider rationaleDialogProvider) {
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            throw new IllegalStateException("You cannot create PermissionProvider without any permission required.");
        }

        this.requiredPermissions = requiredPermissions;
        this.rationalDialogProvider = rationaleDialogProvider;
    }

    /**
     * Return true if it is possible to ask permission, false otherwise
     */
    public abstract boolean requestPermissions();

    /**
     * This method needs to be called when permission results are received
     */
    public abstract void onRequestPermissionsResult(int requestCode,
                                                    @Nullable String[] permissions,
                                                    @NonNull int[] grantResults);

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    @Nullable public DialogProvider getDialogProvider() {
        return rationalDialogProvider;
    }

    @Nullable public PermissionListener getPermissionListener() {
        return weakPermissionListener.get();
    }

    @Nullable protected Context getContext() {
        return weakContextProcessor.get() == null ? null : weakContextProcessor.get().getContext();
    }

    @Nullable protected Activity getActivity() {
        return weakContextProcessor.get() == null ? null : weakContextProcessor.get().getActivity();
    }

    @Nullable protected Fragment getFragment() {
        return weakContextProcessor.get() == null ? null : weakContextProcessor.get().getFragment();
    }

    /**
     * This will be set internally by {@linkplain LocationManager} before any call is executed on PermissionProvider
     */
    @CallSuper
    public void setContextProcessor(ContextProcessor contextProcessor) {
        this.weakContextProcessor = new WeakReference<>(contextProcessor);
    }

    /**
     * This will be set internally by {@linkplain LocationManager} before any call is executed on PermissionProvider
     */
    @CallSuper public void setPermissionListener(PermissionListener permissionListener) {
        this.weakPermissionListener = new WeakReference<>(permissionListener);
    }

    /**
     * Return true if required permissions are granted, false otherwise
     */
    public boolean hasPermission() {
        if (getContext() == null) {
            LogUtils.logE("Couldn't check whether permissions are granted or not "
                    + "because of PermissionProvider doesn't contain any context.");
            return false;
        }

        // 遍历看所需权限是否均已授权
        for (String permission : getRequiredPermissions()) {
            LogUtils.logE("hasPermission: permission: " + permission);
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 检验该项权限是否授权
    protected int checkSelfPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission);
    }

}
