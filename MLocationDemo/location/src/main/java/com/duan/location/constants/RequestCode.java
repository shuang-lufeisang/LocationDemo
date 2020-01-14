package com.duan.location.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.duan.location.constants.RequestCode.GOOGLE_PLAY_SERVICES;
import static com.duan.location.constants.RequestCode.GPS_ENABLE;
import static com.duan.location.constants.RequestCode.RUNTIME_PERMISSION;
import static com.duan.location.constants.RequestCode.SETTINGS_API;
import static com.duan.location.constants.RequestCode.WIFI_ENABLE;

@IntDef({RUNTIME_PERMISSION, GOOGLE_PLAY_SERVICES,
        GPS_ENABLE, WIFI_ENABLE, SETTINGS_API})
@Retention(RetentionPolicy.SOURCE)
public @interface RequestCode {

    int RUNTIME_PERMISSION = 23;
    int GOOGLE_PLAY_SERVICES = 24;
    int GPS_ENABLE = 25;
    int WIFI_ENABLE = 27; // wifi发射高频率电磁波用于卫星接收定位
    int SETTINGS_API = 26;

}
