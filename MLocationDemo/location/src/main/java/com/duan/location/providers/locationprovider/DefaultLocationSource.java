package com.duan.location.providers.locationprovider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.duan.location.helper.ContinuousTask;
import com.duan.location.helper.UpdateRequest;
import com.duan.location.utils.LogUtils;

import java.util.Date;

public class DefaultLocationSource {

    static final String PROVIDER_SWITCH_TASK = "providerSwitchTask";

    private LocationManager locationManager;
    private UpdateRequest updateRequest;
    private ContinuousTask cancelTask;

    void createLocationManager(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    void createUpdateRequest(LocationListener locationListener) {
        updateRequest = new UpdateRequest(locationManager, locationListener);
    }

    void createProviderSwitchTask(ContinuousTask.ContinuousTaskRunner continuousTaskRunner) {
        this.cancelTask = new ContinuousTask(PROVIDER_SWITCH_TASK, continuousTaskRunner);
    }

    boolean isProviderEnabled(String provider) {
        return locationManager.isProviderEnabled(provider);
    }

    @SuppressWarnings("ResourceType")
    Location getLastKnownLocation(String provider) {
        return locationManager.getLastKnownLocation(provider);
    }

    @SuppressWarnings("ResourceType")
    void removeLocationUpdates(LocationListener locationListener) {
        locationManager.removeUpdates(locationListener);
    }

    void removeUpdateRequest() {
        updateRequest.release();
        updateRequest = null;
    }

    void removeSwitchTask() {
        cancelTask.stop();
        cancelTask = null;
    }

    boolean switchTaskIsRemoved() {
        return cancelTask == null;
    }

    boolean updateRequestIsRemoved() {
        return updateRequest == null;
    }

    ContinuousTask getProviderSwitchTask() {
        return cancelTask;
    }

    UpdateRequest getUpdateRequest() {
        return updateRequest;
    }

    // 定位信息是否充分
    boolean isLocationSufficient(Location location, long acceptableTimePeriod, float acceptableAccuracy) {
        LogUtils.logE("=======  isLocationSufficient ========");
        if (location == null) return false;

        float givenAccuracy = location.getAccuracy();
        long givenTime = location.getTime();
        long minAcceptableTime = new Date().getTime() - acceptableTimePeriod; // 最小可接受时间

        return minAcceptableTime <= givenTime && acceptableAccuracy >= givenAccuracy;
    }


}
