package com.duan.mlocationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duan.mlocationdemo.activity.LocationActivity;
import com.duan.mlocationdemo.databinding.ActivityMainBinding;
import com.duan.mlocationdemo.fragment.LocationFragmentActivity;
import com.duan.mlocationdemo.service.LocationService;
import com.duan.mlocationdemo.service.LocationServiceActivity;
import com.duan.mlocationdemo.utils.LogUtils;

import java.io.IOException;
import java.util.List;

/**
 *  定位方式有三种
 *
 *        第一种是网络定位    网络定位是通过IP定位
 *
 *        第二种是基站定位    基站定位是通过每一个信号源定位
 *
 *        第三中是 GSP 定位   卫星定位
 */
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;

    String TAG = "Location_MainActivity";
    String TAG_Permission = "Location_Permission_MainActivity";

    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // 定位管理器
//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        initPermissions();

        startService(new Intent(this, LocationService.class));

        //通过最好的位置获得经纬度
        //参数一  定位方式
        //参数二  参数二是  隔多长时间调用  回调
        //参数三  移动多少米调用   回调

    }

    public void showPromptMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, message);
    }

    // 获取定位权限
    PackageManager pkgManager;
    boolean fineLocationPermission;
    boolean coarseLocationPermission;

    private void initPermissions() {
        pkgManager = getPackageManager();  // 包名管理
        fineLocationPermission =
                pkgManager.checkPermission(
                        android.Manifest.permission.ACCESS_FINE_LOCATION, getPackageName())
                        == PackageManager.PERMISSION_GRANTED;

        coarseLocationPermission =
                pkgManager.checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName())
                        == PackageManager.PERMISSION_GRANTED;

        if (!fineLocationPermission || !coarseLocationPermission) { // Build.VERSION.SDK_INT >= 23 &&
            LogUtils.printError(TAG_Permission, "initPermissions 未授权 ！");
            requestPermission(); // initPermissions
        } else {
            LogUtils.printError(TAG_Permission, "initPermissions 已授权");
            //initLocation();
            //getLocationCity();   // 已授权 ：获取当前定位城市
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 0;

    private void requestPermission() {
        LogUtils.printError(TAG, "============ requestPermission =============");

        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                },
                PERMISSION_REQUEST_CODE
        );
    }

    // 权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            //根据用户点击判断，是否点击授权或者拒绝，做不同的操作
            for (String permission : permissions) {
                LogUtils.printCloseableInfo(TAG_Permission, "onRequestPermissionsResult permission: " + permission);
            }
            for (int grantResult : grantResults) {
                LogUtils.printCloseableInfo(TAG_Permission, "onRequestPermissionsResult grantResult: " + grantResult);
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                initLocation();
//                 getLocationCity();    // 已授权 ：获取当前定位城市 onRequestPermissionsResult

            } else {
                showPromptMessage("请先授权再使用");
            }
            return;
        }
    }

    private void initLocation() {
        Log.e(TAG, "=====  initLocation =====");
        Criteria criteria = new Criteria();
        // 定位的精度，一般精度越高就约精准，并且耗费电量越高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.ACCURACY_HIGH);
        String provider = mLocationManager.getBestProvider(criteria, true); // 获取最好的定位方式
        Log.d(TAG, "provider = " + provider);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.e(TAG, "===== initLocation  checkSelfPermission  =====");
            requestPermission();
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 0, 10, new LocationListener() {
            // 位置改变调用的方法    即经纬度改变时调用
            @Override
            public void onLocationChanged(Location location) {
                //获得精度
                double longitude = location.getLongitude();
                //获得维度
                double latitude = location.getLatitude();
                TextView view = new TextView(getApplicationContext());
                view.setText("精度=" + longitude + "     维度=" + latitude);
                view.setTextColor(Color.BLACK);
                setContentView(view);
                Log.e(TAG, "精度=" + longitude + "     维度=" + latitude);

                mBinding.tv.setText("精度=" + longitude + "     维度=" + latitude);
            }

            // Provider 状态在可用、暂不可用、无服务三个状态之间直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                showPromptMessage("== onStatusChanged ==");
            }

            // Provider被enable时触发此函数,比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {
                showPromptMessage("== onProviderEnabled ==");
            }

            // Provider被disable时触发此函数,比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {
                showPromptMessage("== onProviderDisabled ==");
            }
        });

    }

    // 点击获取经纬度 - 当地城市

    public void getLocationCity() {

        Log.e(TAG, "=====  点击获取经纬度 - 当地城市 =====");
        LogUtils.printError(TAG_Permission, "getLocationCity 权限获取成功后/点击获取经纬度");
        new Thread() {
            @Override
            public void run() {
                //  getLocalCityData();
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    LogUtils.printCloseableInfo(TAG, "getLocationCity 未授权");
                    requestPermission();
                }else {
                    Location location = null;
                    LogUtils.printCloseableInfo(TAG_Permission, "getLocationCity 已授权");
//                    Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    LogUtils.printError(TAG, "=== mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) === 维度："+location.getLatitude() + " 经度："+ location.getLongitude());

                    Criteria criteria = new Criteria();
                    // 定位的精度，一般精度越高就约精准，并且耗费电量越高
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setPowerRequirement(Criteria.ACCURACY_HIGH);
                    String provider = mLocationManager.getBestProvider(criteria, true); // 获取最好的定位方式
                    Log.d(TAG, "provider = " + provider);

                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            LogUtils.printError(TAG, "=== onLocationChanged === 维度："+location.getLatitude() + " 经度："+ location.getLongitude());
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                            showPromptMessage("onStatusChanged");
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            showPromptMessage("onProviderEnabled");
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            showPromptMessage("onProviderDisabled");
                        }
                    });


                    Location location_gps = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location location_location = mLocationManager.getLastKnownLocation(LocationManager.KEY_LOCATION_CHANGED);
                    if (location == null){
                        LogUtils.printError(TAG_Permission,"location == null");
                    }
                    if (location_gps == null){
                        LogUtils.printError(TAG_Permission,"location_gps == null");
                    }
                    if (location_location == null){
                        LogUtils.printError(TAG_Permission,"location_location == null");
                    }

                    if (location != null) {

                        double longitude = location.getLongitude(); // 经度
                        double latitude = location.getLatitude();   // 纬度

                        // 将经纬度转换成中文地址

                        List<Address> addList = null;
                        Geocoder ge = new Geocoder(getApplicationContext());
                        try {
                            addList = ge.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        String locationCity = "重新定位！";
                        String locationCode = "310100";
                        if (addList != null && addList.size() > 0) {
                            for (int i = 0; i < addList.size(); i++) {
                                Address address = addList.get(i);
                                locationCity = address.getAdminArea();
                            }
                        }else {
                            Log.e(TAG, "addList== null||size=0 " );
                        }


                        String[] data = {locationCity, String.valueOf(latitude), String.valueOf(longitude)};
//                        String[] data = {locationCity, locationCode};
                        Message msg = handler.obtainMessage();
                        // msg.obj = locationCity;
                        msg.obj = data;
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                    }else {
                        LogUtils.printError(TAG_Permission,"location == null");
                    }
                }

            }
        }.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1){
                case 2:
                    LogUtils.printError(TAG, "========= handleMessage ==========");
                    String[] data = (String[]) msg.obj;
                    String locationCity = data[0];
                    String latitude = data[1];
                    String longitude = data[2];
                    showPromptMessage("handler: "+locationCity);
                    mBinding.tv.setText(locationCity+ " 经度=" +longitude  + "     维度=" + latitude);
                    break;
            }
        }
    };

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void inActivityClick(View view) {
        startActivity(new Intent(this, LocationActivity.class));
    }

    public void inFragmentClick(View view) {
        startActivity(new Intent(this, LocationFragmentActivity.class));
    }

    public void inServiceClick(View view) {
        startActivity(new Intent(this, LocationServiceActivity.class));
    }

}
