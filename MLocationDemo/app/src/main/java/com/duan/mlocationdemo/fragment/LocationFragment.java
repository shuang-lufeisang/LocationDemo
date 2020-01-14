package com.duan.mlocationdemo.fragment;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.duan.location.base.LocationBaseFragment;
import com.duan.location.configuration.Configurations;
import com.duan.location.configuration.LocationConfiguration;
import com.duan.location.utils.LogUtils;
import com.duan.mlocationdemo.LocationPresenter;
import com.duan.mlocationdemo.R;
import com.duan.mlocationdemo.application.MyApplication;

public class LocationFragment extends LocationBaseFragment implements LocationPresenter.LocationView {

    private ProgressDialog progressDialog;
    private TextView locationText;
    private LocationPresenter mLocationPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationPresenter = new LocationPresenter(this);
        getLocation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.location_display_layout, null);
        View view = inflater.inflate(R.layout.location_display_layout, container, false);
        locationText =  view.findViewById(R.id.locationText);
        LogUtils.logE("onCreateView");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLocationPresenter != null) mLocationPresenter.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 正在等待定位 && 没有其他弹窗正在显示 => 展示弹窗
        if (getLocationManager().isWaitingForLocation() && !getLocationManager().isAnyDialogShowing()){
            displayProgress();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgress();
    }

    private void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MyApplication.sApplication);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage(getString(R.string.getting_location));
        }

        if (!progressDialog.isShowing() && getActivity() != null) {
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
