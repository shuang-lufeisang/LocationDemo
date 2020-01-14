package com.duan.mlocationdemo.activity;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.duan.location.base.LocationBaseActivity;
import com.duan.location.configuration.Configurations;
import com.duan.location.configuration.LocationConfiguration;
import com.duan.mlocationdemo.LocationPresenter;
import com.duan.mlocationdemo.R;

public class LocationActivity extends LocationBaseActivity implements LocationPresenter.LocationView {

    private ProgressDialog progressDialog;
    private TextView locationText;

    private LocationPresenter mLocationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_display_layout);

        locationText = findViewById(R.id.locationText);
        mLocationPresenter = new LocationPresenter(this);
        getLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationPresenter.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 正在等待定位 && 没有其他弹窗正在显示 => 展示弹窗
        if (getLocationManager().isWaitingForLocation() && !getLocationManager().isAnyDialogShowing()){
            displayProgress();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgress();
    }

    private void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage(getString(R.string.getting_location));
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return Configurations.defaultConfiguration(getString(R.string.give_me_permission), getString(R.string.please_turn_gps_on));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationPresenter.onLocationChanged(location);
    }

    @Override
    public void onLocationFailed(int type) {
        mLocationPresenter.onLocationFailed(type);
    }

    @Override
    public void onProcessTypeChanged(int processType) {
        //super.onProcessTypeChanged(processType);
        mLocationPresenter.onProcessTypeChanged(processType);
    }

    @Override
    public String getText() {
        return locationText.getText().toString();
    }

    @Override
    public void setText(String text) {
        locationText.setText(text);
    }

    @Override
    public void updateProgress(String text) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(text);
        }
    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
