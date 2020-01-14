package com.duan.mlocationdemo;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.duan.location.constants.FailType;
import com.duan.location.constants.ProcessType;
import com.duan.mlocationdemo.application.MyApplication;

import java.io.IOException;
import java.util.List;

public class LocationPresenter {

    String TAG = "LocationPresenter";
    private LocationView mLocationView;


    public LocationPresenter(LocationView view) {
        this.mLocationView = view;
        geocoder = new Geocoder(MyApplication.sApplication);
    }

    public void destroy() {
        mLocationView = null;
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "==== onLocationChanged ===== "+ location.toString());
        mLocationView.dismissProgress();
        setText(location);
    }

    public void onLocationFailed(@FailType int failType) {
        mLocationView.dismissProgress();

        switch (failType) {
            case FailType.TIMEOUT: {
                mLocationView.setText("Couldn't get location, and timeout!");
                break;
            }
            case FailType.PERMISSION_DENIED: {
                mLocationView.setText("Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                mLocationView.setText("Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE: {
                mLocationView.setText("Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL: {
                mLocationView.setText("Couldn't get location, because Google Play Services connection failed!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG: {
                mLocationView.setText("Couldn't display settingsApi dialog!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DENIED: {
                mLocationView.setText("Couldn't get location, because user didn't activate providers via settingsApi!");
                break;
            }
            case FailType.VIEW_DETACHED: {
                mLocationView.setText("Couldn't get location, because in the process view was detached!");
                break;
            }
            case FailType.VIEW_NOT_REQUIRED_TYPE: {
                mLocationView.setText("Couldn't get location, "
                        + "because view wasn't sufficient enough to fulfill given configuration!");
                break;
            }
            case FailType.UNKNOWN: {
                mLocationView.setText("Ops! Something went wrong!");
                break;
            }
        }
    }

    public void onProcessTypeChanged(@ProcessType int newProcess) {
        switch (newProcess) {
            case ProcessType.GETTING_LOCATION_FROM_GOOGLE_PLAY_SERVICES: {
                mLocationView.updateProgress("Getting Location from Google Play Services...");
                break;
            }
            case ProcessType.GETTING_LOCATION_FROM_GPS_PROVIDER: {
                mLocationView.updateProgress("Getting Location from GPS...");
                break;
            }
            case ProcessType.GETTING_LOCATION_FROM_NETWORK_PROVIDER: {
                mLocationView.updateProgress("Getting Location from Network...");
                break;
            }
            case ProcessType.ASKING_PERMISSIONS:
            case ProcessType.GETTING_LOCATION_FROM_CUSTOM_PROVIDER:
                // Ignored
                break;
        }
    }

    Geocoder geocoder;
    private void setText(Location location) {
        long startTime =  System.currentTimeMillis();
        double latitude = location.getLatitude();   // 经度
        double longitude = location.getLongitude(); // 纬度
        /**
         * 将经纬度转换成中文地址
         */
        List<Address> addressList = null;
        //Geocoder geocoder = new Geocoder(SampleApplication.sApplication);
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String locationCity = "定位失败，点击重新定位！";
        String locationCode = "310100";

        if (addressList != null && addressList.size() > 0) {
            for (int i = 0; i < addressList.size(); i++) {
                Address address = addressList.get(i);
                locationCity = address.getAdminArea();
                Log.e(TAG, "locationCity: " + locationCity );

                //locationCode = mPresenter.getCodeByCity(locationCity);
            }
        }else {
            Log.e(TAG, "addList== null||size=0 " );
        }

        String appendValue = "(经度: "+ location.getLatitude() + "  纬度: " + location.getLongitude() ;
        appendValue = locationCity + appendValue  + ")\n\n";
        String newValue;
        CharSequence current = mLocationView.getText();

        if (!TextUtils.isEmpty(current)) {
            newValue = current + appendValue;
        } else {
            newValue = appendValue;
        }

        mLocationView.setText(newValue);
        long endTime =  System.currentTimeMillis();
        long consuming = endTime - startTime;
        Log.e(TAG, "耗时(毫秒)：" + consuming);

    }


    public interface LocationView {

        String getText();                // 获取当前展示的定位信息

        void setText(String text);       // 设置当前定位信息

        void updateProgress(String text);// 更新弹窗（获取定位中）

        void dismissProgress();          // 关闭弹窗（获取定位中）

    }


}
