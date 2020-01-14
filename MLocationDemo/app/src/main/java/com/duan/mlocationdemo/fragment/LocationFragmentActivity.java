package com.duan.mlocationdemo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duan.location.utils.LogUtils;
import com.duan.mlocationdemo.R;

public class LocationFragmentActivity extends AppCompatActivity {

    LocationFragment locationFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView( R.layout.activity_location_fragment);
        LogUtils.logE("onCreate");
        DataBindingUtil.setContentView(this, R.layout.activity_location_fragment);
        showLocationFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.logE("onActivityResult");
        dispatchToFragment(requestCode, resultCode, data);
    }

    /**
     * This is required because GooglePlayServicesApi and SettingsApi requires Activity,
     * and they call startActivityForResult from the activity, not fragment,
     * fragment doesn't receive onActivityResult callback. We need to call/redirect manually.
     */
    private void dispatchToFragment(int requestCode, int resultCode, Intent data) {
//        LocationFragment locationFragment = (LocationFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (locationFragment != null) {
            locationFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    FragmentManager fm;
    private void showLocationFragment(){
        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (locationFragment == null){
            locationFragment = new LocationFragment();
            transaction.add(R.id.frame_layout, locationFragment);
        }
        transaction.show(locationFragment);
        transaction.commit();
    }


}
