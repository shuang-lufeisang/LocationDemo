package com.duan.location.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.duan.location.constants.FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL;
import static com.duan.location.constants.FailType.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE;
import static com.duan.location.constants.FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DENIED;
import static com.duan.location.constants.FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG;
import static com.duan.location.constants.FailType.NETWORK_NOT_AVAILABLE;
import static com.duan.location.constants.FailType.PERMISSION_DENIED;
import static com.duan.location.constants.FailType.TIMEOUT;
import static com.duan.location.constants.FailType.UNKNOWN;
import static com.duan.location.constants.FailType.VIEW_DETACHED;
import static com.duan.location.constants.FailType.VIEW_NOT_REQUIRED_TYPE;

/**
 * <pre>
 * author : Duan
 * time : 2020/01/07
 * desc :  定位失败类型
 * version: 1.0.0
 * </pre>
 */

@IntDef({UNKNOWN,TIMEOUT,PERMISSION_DENIED,NETWORK_NOT_AVAILABLE,
        GOOGLE_PLAY_SERVICES_NOT_AVAILABLE,
        GOOGLE_PLAY_SERVICES_CONNECTION_FAIL,
        GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG,
        GOOGLE_PLAY_SERVICES_SETTINGS_DENIED,
        VIEW_DETACHED,VIEW_NOT_REQUIRED_TYPE
})
@Retention(RetentionPolicy.SOURCE)
public @interface FailType {

    int UNKNOWN = -1;
    int TIMEOUT = 1;
    int PERMISSION_DENIED = 2;
    int NETWORK_NOT_AVAILABLE = 3;
    int GOOGLE_PLAY_SERVICES_NOT_AVAILABLE = 4;
    int GOOGLE_PLAY_SERVICES_CONNECTION_FAIL = 5;
    int GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG = 6;
    int GOOGLE_PLAY_SERVICES_SETTINGS_DENIED = 7;
    int VIEW_DETACHED = 8;
    int VIEW_NOT_REQUIRED_TYPE = 9;
}
