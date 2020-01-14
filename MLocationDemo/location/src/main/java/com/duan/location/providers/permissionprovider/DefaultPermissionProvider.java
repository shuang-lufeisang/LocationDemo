package com.duan.location.providers.permissionprovider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duan.location.LocationManager;
import com.duan.location.constants.RequestCode;
import com.duan.location.listener.DialogListener;
import com.duan.location.providers.dialogprovider.DialogProvider;
import com.duan.location.utils.LogUtils;

public class DefaultPermissionProvider extends PermissionProvider implements DialogListener {

    private PermissionCompatSource permissionCompatSource;

    /**
     * This class is responsible to get required permissions, and notify {@linkplain LocationManager}.
     *
     * @param requiredPermissions     are required, setting this field empty will {@throws IllegalStateException}
     * @param rationaleDialogProvider will be used to display rationale dialog when it is necessary. If this field is set
     */
    public DefaultPermissionProvider(String[] requiredPermissions, @Nullable DialogProvider rationaleDialogProvider) {
        super(requiredPermissions, rationaleDialogProvider);
    }

    @Override
    public boolean requestPermissions() {
        if (getActivity() == null) {
            LogUtils.logI("Cannot ask for permissions, "
                    + "because DefaultPermissionProvider doesn't contain an Activity instance.");
            return false;
        }

        if (shouldShowRequestPermissionRationale()) {
            getDialogProvider().setDialogListener(this);
            getDialogProvider().getDialog(getActivity()).show();
        } else {
            executePermissionsRequest();
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @NonNull int[] grantResults) {

    }

    //  implements DialogListener
    @Override
    public void onPositiveButtonClick() {

    }

    //  implements DialogListener
    @Override
    public void onNegativeButtonClick() {

    }



    // 是否展示权限申请理由
    boolean shouldShowRequestPermissionRationale() {
        boolean shouldShowRationale = false;  // 是否展示权限申请理由
        for (String permission : getRequiredPermissions()) {
            shouldShowRationale = shouldShowRationale || checkRationaleForPermission(permission);
        }

        LogUtils.logI("Should show rationale dialog for required permissions: " + shouldShowRationale);

        return shouldShowRationale && getActivity() != null && getDialogProvider() != null;
    }

    boolean checkRationaleForPermission(String permission) {
        if (getFragment() != null) {
            return getPermissionCompatSource().shouldShowRequestPermissionRationale(getFragment(), permission);
        } else if (getActivity() != null) {
            return getPermissionCompatSource().shouldShowRequestPermissionRationale(getActivity(), permission);
        } else {
            return false;
        }
    }

    void executePermissionsRequest() {
        LogUtils.logI("Asking for Runtime Permissions...");
        if (getFragment() != null) {
            getPermissionCompatSource().requestPermissions(getFragment(),
                    getRequiredPermissions(), RequestCode.RUNTIME_PERMISSION);
        } else if (getActivity() != null) {
            getPermissionCompatSource().requestPermissions(getActivity(),
                    getRequiredPermissions(), RequestCode.RUNTIME_PERMISSION);
        } else {
            LogUtils.logE("Something went wrong requesting for permissions.");
            if (getPermissionListener() != null) getPermissionListener().onPermissionsDenied();
        }
    }

    // For test purposes
    void setPermissionCompatSource(PermissionCompatSource permissionCompatSource) {
        this.permissionCompatSource = permissionCompatSource;
    }

    protected PermissionCompatSource getPermissionCompatSource() {
        if (permissionCompatSource == null) {
            permissionCompatSource = new PermissionCompatSource();
        }
        return permissionCompatSource;
    }
}
