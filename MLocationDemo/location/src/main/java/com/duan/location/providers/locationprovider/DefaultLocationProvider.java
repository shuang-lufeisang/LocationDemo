package com.duan.location.providers.locationprovider;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.duan.location.constants.FailType;
import com.duan.location.constants.ProcessType;
import com.duan.location.constants.RequestCode;
import com.duan.location.helper.ContinuousTask;
import com.duan.location.listener.DialogListener;
import com.duan.location.providers.dialogprovider.DialogProvider;
import com.duan.location.utils.LogUtils;

public class DefaultLocationProvider extends LocationProvider implements ContinuousTask.ContinuousTaskRunner, LocationListener, DialogListener {

    private DefaultLocationSource defaultLocationSource;

    private String provider;
    private Dialog gpsDialog;

    @Override
    public void initialize() {
        super.initialize();

        getSourceProvider().createLocationManager(getContext());
        getSourceProvider().createProviderSwitchTask(this);
        getSourceProvider().createUpdateRequest(this); // LocationListener
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        gpsDialog = null;

        getSourceProvider().removeSwitchTask();
        getSourceProvider().removeUpdateRequest();
        getSourceProvider().removeLocationUpdates(this);// LocationListener
    }

    @Override
    public void cancel() {
        getSourceProvider().getUpdateRequest().release();
        getSourceProvider().getProviderSwitchTask().stop();
    }

    @Override
    public void onPause() {
        super.onPause();

        getSourceProvider().getUpdateRequest().release();
        getSourceProvider().getProviderSwitchTask().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        getSourceProvider().getUpdateRequest().run();

        if (isWaiting()) {
            getSourceProvider().getProviderSwitchTask().resume();
        }

        if (isDialogShowing() && isGPSProviderEnabled()) {
            // User activated GPS by going settings manually
            gpsDialog.dismiss();
            onGPSActivated();
        }
    }

    @Override
    public boolean isDialogShowing() {
        return gpsDialog != null && gpsDialog.isShowing();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.logE("======== onActivityResult =======");
        // GPS
        if (requestCode == RequestCode.GPS_ENABLE) {
            if (isGPSProviderEnabled()) {
                onGPSActivated();
            } else {
                LogUtils.logI("User didn't activate GPS, so continue with Network Provider");
                getLocationByNetwork();
            }
        }

        // TODO: 2020/1/6  WIFI
    }

    private long startGPS, endGPS;
    @Override
    public void get() {
        LogUtils.logE("=====  get()  =====");
        setWaiting(true);

        // First check for GPS
        if (isGPSProviderEnabled()) {
            LogUtils.logE("GPS is already enabled, getting location...");
            startGPS = System.currentTimeMillis();
            askForLocation(LocationManager.GPS_PROVIDER);
        } else {
            // GPS is not enabled,
            if (getConfiguration().defaultProviderConfiguration().askForEnableGPS() && getActivity() != null) {
                LogUtils.logI("GPS is not enabled, asking user to enable it...");
                askForEnableGPS();
            } else {
                LogUtils.logI("GPS is not enabled, moving on with Network...");
                getLocationByNetwork();
            }
        }
    }

    void askForEnableGPS() {
        DialogProvider gpsDialogProvider = getConfiguration().defaultProviderConfiguration().gpsDialogProvider();
        gpsDialogProvider.setDialogListener(this);

        gpsDialog = gpsDialogProvider.getDialog(getActivity());
        gpsDialog.show();
    }

    void onGPSActivated() {
        LogUtils.logI("User activated GPS, listen for location");
        askForLocation(LocationManager.GPS_PROVIDER);
    }

    // 通过网络获取定位
    void getLocationByNetwork() {
        if (isNetworkProviderEnabled()) {
            LogUtils.logI("Network is enabled, getting location...");
            askForLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            LogUtils.logI("Network is not enabled, calling fail...");
            onLocationFailed(FailType.NETWORK_NOT_AVAILABLE);
        }
    }

    // 请求定位
    void askForLocation(String provider) {
        LogUtils.logI("-----------------------------");
        LogUtils.logI("------------- askForLocation ---------------- provider: " + provider);
        getSourceProvider().getProviderSwitchTask().stop();
        setCurrentProvider(provider);

        boolean locationIsAlreadyAvailable = checkForLastKnowLocation();

        if (getConfiguration().keepTracking() || !locationIsAlreadyAvailable) {
            LogUtils.logI("Ask for location update...");
            notifyProcessChange();
            // Ask for immediate location update
            requestUpdateLocation(0, 0, !locationIsAlreadyAvailable);
        } else {
            LogUtils.logI("We got location, no need to ask for location updates.");
        }
    }

    // 上次定位位置
    boolean checkForLastKnowLocation() {
        Location lastKnownLocation = getSourceProvider().getLastKnownLocation(provider);

        if (getSourceProvider().isLocationSufficient(
                lastKnownLocation,
                getConfiguration().defaultProviderConfiguration().acceptableTimePeriod(),
                getConfiguration().defaultProviderConfiguration().acceptableAccuracy())) {

            LogUtils.logI("LastKnowLocation is usable.");
            onLocationReceived(lastKnownLocation);
            return true;
        } else {
            LogUtils.logI("LastKnowLocation is not usable.");
        }

        return false;
    }

    void setCurrentProvider(String provider) {
        this.provider = provider;
    }

    void notifyProcessChange() {
        if (getListener() != null) {
            getListener().onProcessTypeChanged(LocationManager.GPS_PROVIDER.equals(provider)
                    ? ProcessType.GETTING_LOCATION_FROM_GPS_PROVIDER
                    : ProcessType.GETTING_LOCATION_FROM_NETWORK_PROVIDER);
        }
    }

    void requestUpdateLocation(long timeInterval, long distanceInterval, boolean setCancelTask) {
        startGPS = System.currentTimeMillis();
        LogUtils.logE("requestUpdateLocation startGPS " + startGPS + "  setCancelTask: " + setCancelTask);
        if (setCancelTask) {
            getSourceProvider().getProviderSwitchTask().delayed(getWaitPeriod());
        }
        getSourceProvider().getUpdateRequest().run(provider, timeInterval, distanceInterval);
    }

    long getWaitPeriod() {
        return LocationManager.GPS_PROVIDER.equals(provider)
                ? getConfiguration().defaultProviderConfiguration().gpsWaitPeriod()
                : getConfiguration().defaultProviderConfiguration().networkWaitPeriod();
    }


    // WIFI 是否打开
    private boolean isNetworkProviderEnabled() {
        return getSourceProvider().isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // GPS 是否打开
    private boolean isGPSProviderEnabled() {
        return getSourceProvider().isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 获取到定位
    void onLocationReceived(Location location) {
        LogUtils.logE("======= onLocationReceived =======");
        if (getListener() != null) {
            getListener().onLocationChanged(location);
        }
        setWaiting(false);
    }

    void onLocationFailed(@FailType int type) {
        if (getListener() != null) {
            getListener().onLocationFailed(type);
        }
        setWaiting(false);
    }



    /** start LocationListener ******************/
    @Override
    public void onLocationChanged(Location location) {
        LogUtils.logE("============== onLocationChanged ================");
        Log.i("locationChanged","============== onLocationChanged ================");
        //ToastUtil.show(getContext(), "onLocationChanged!");

        if (getSourceProvider().updateRequestIsRemoved()) {
            return;
        }
        onLocationReceived(location);

        // Remove cancelLocationTask because we have already find location,
        // no need to switch or call fail
        if (!getSourceProvider().switchTaskIsRemoved()) {
            getSourceProvider().getProviderSwitchTask().stop();
        }

        // Remove update requests if it is running for immediate request
        if (getSourceProvider().getUpdateRequest().isRequiredImmediately() || !getConfiguration().keepTracking()) {
            getSourceProvider().removeLocationUpdates(this);
        }

        if (getConfiguration().keepTracking()) {
            requestUpdateLocation(getConfiguration().defaultProviderConfiguration().requiredTimeInterval(),
                    getConfiguration().defaultProviderConfiguration().requiredDistanceInterval(), false);
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (getListener() != null) {
            getListener().onStatusChanged(provider, status, extras);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (getListener() != null) {
            getListener().onProviderEnabled(provider);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (getListener() != null) {
            getListener().onProviderDisabled(provider);
        }
    }

    /** end LocationListener ******************/

    @Override
    public void runScheduledTask(@NonNull String taskId) {
        if (taskId.equals(DefaultLocationSource.PROVIDER_SWITCH_TASK)) {
            getSourceProvider().getUpdateRequest().release();

            endGPS = System.currentTimeMillis();
            long timeConsuming = endGPS - startGPS;
            LogUtils.logE("time consuming for GPS: " + timeConsuming);
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                LogUtils.logI("We waited enough for GPS, switching to Network provider...");
                getLocationByNetwork();
            } else {
                LogUtils.logI("Network Provider is not provide location in required period, calling fail...");
                onLocationFailed(FailType.TIMEOUT);
            }
        }
    }

    @Override
    public void onPositiveButtonClick() {

//        boolean activityStarted = startActivityForResult(
//                new Intent(Settings.ACTION_WIFI_SETTINGS),
//                RequestCode.WIFI_ENABLE
//        ); // 引导用户去设置里打开WIFI

        boolean activityStarted = startActivityForResult(
                new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                RequestCode.GPS_ENABLE
        ); // 引导用户去设置里打开GPS

        if (!activityStarted) {
            onLocationFailed(FailType.VIEW_NOT_REQUIRED_TYPE);
        }
    }

    @Override
    public void onNegativeButtonClick() {
        LogUtils.logI("User didn't want to enable GPS, so continue with Network Provider");
        getLocationByNetwork();
    }

    // For test purposes
    void setDefaultLocationSource(DefaultLocationSource defaultLocationSource) {
        this.defaultLocationSource = defaultLocationSource;
    }

    private DefaultLocationSource getSourceProvider() {
        if (defaultLocationSource == null) {
            defaultLocationSource = new DefaultLocationSource();
        }
        return defaultLocationSource;
    }


}
