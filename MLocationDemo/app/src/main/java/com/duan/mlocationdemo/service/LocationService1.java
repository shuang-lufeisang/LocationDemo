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

public class LocationService1 extends Service {

    private static final String TAG = "LocationService";
    private SharedPreferences sp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) {
            Log.d(TAG, "provider equals null");
            return;
        }

        LocationService1.MyListener listener = new LocationService1.MyListener();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.d(TAG, "未授权");
            return;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, listener);

        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null){
            Log.d(TAG, "location equals null");
            return;
        }

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        saveLocation(location);

        Log.d(TAG, "locationService");
    }

    public void saveLocation(Location location){
        double longitude = location.getLongitude();
        double altitude = location.getLatitude();
        float accuracy = location.getAccuracy();

        String pos = "j:" + longitude + "\tw:" + altitude
                + "\ta:" + accuracy + "\n";
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("location", pos);
        edit.commit();
        Log.d(TAG, pos);
    }

    private class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            saveLocation(location);
            Log.d(TAG, "onLocationChanged");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }
    }
}

