package com.duan.mlocationdemo.application;

import android.app.Application;

import com.duan.location.LocationManager;

public class MyApplication extends Application {

    public static Application sApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        LocationManager.enableLog(true);
    }
}
