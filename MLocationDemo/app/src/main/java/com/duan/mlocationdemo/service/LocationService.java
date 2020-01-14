package com.duan.mlocationdemo.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.duan.location.base.LocationBaseService;
import com.duan.location.configuration.Configurations;
import com.duan.location.configuration.LocationConfiguration;
import com.duan.location.constants.FailType;
import com.duan.location.constants.ProcessType;
import com.duan.location.utils.LogUtils;

/**
 * <pre>
 * author : Duan
 * time : 2019/12/20
 * desc :
 * version: 2.3.3
 * </pre>
 */
public class LocationService extends LocationBaseService {

    private static final String TAG = "LocationService";

    public static final String ACTION_LOCATION_CHANGED = "com.duan.mlocationdemo.service.LOCATION_CHANGED";
    public static final String ACTION_LOCATION_FAILED = "com.duan.mlocationdemo.service.LOCATION_FAILED";
    public static final String ACTION_PROCESS_CHANGED = "com.duan.mlocationdemo.service.PROCESS_CHANGED";

    public static final String EXTRA_LOCATION = "ExtraLocationField";
    public static final String EXTRA_FAIL_TYPE = "ExtraFailTypeField";
    public static final String EXTRA_PROCESS_TYPE = "ExtraProcessTypeField";

    private boolean isLocationRequested = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
//        return Configurations.defaultConfiguration("Gimme the permission!", "Would you mind to turn GPS on?");
        return Configurations.silentConfiguration(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // calling super is required when extending from LocationBaseService
        super.onStartCommand(intent, flags, startId);

        if(!isLocationRequested) {
            isLocationRequested = true;
            getLocation();
        }

        // Return type is depends on your requirements
        return START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent(ACTION_LOCATION_CHANGED);
        intent.putExtra(EXTRA_LOCATION, location);
        sendBroadcast(intent);
        LogUtils.logE("====  onLocationChanged  =====");
        stopSelf();
    }

    @Override
    public void onLocationFailed(@FailType int type) {
        Intent intent = new Intent(ACTION_LOCATION_FAILED);
        intent.putExtra(EXTRA_FAIL_TYPE, type);
        sendBroadcast(intent);
        LogUtils.logE("====  onLocationFailed  =====");
        stopSelf();
    }

    @Override
    public void onProcessTypeChanged(@ProcessType int processType) {
        Intent intent = new Intent(ACTION_PROCESS_CHANGED);
        intent.putExtra(EXTRA_PROCESS_TYPE, processType);
        sendBroadcast(intent);
        LogUtils.logE("====  onProcessTypeChanged  =====");
    }

}
