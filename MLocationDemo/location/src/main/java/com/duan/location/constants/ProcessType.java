package com.duan.location.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.duan.location.constants.ProcessType.ASKING_PERMISSIONS;
import static com.duan.location.constants.ProcessType.GETTING_LOCATION_FROM_CUSTOM_PROVIDER;
import static com.duan.location.constants.ProcessType.GETTING_LOCATION_FROM_GOOGLE_PLAY_SERVICES;
import static com.duan.location.constants.ProcessType.GETTING_LOCATION_FROM_GPS_PROVIDER;
import static com.duan.location.constants.ProcessType.GETTING_LOCATION_FROM_NETWORK_PROVIDER;

/**
 * <pre>
 * author : Duan
 * time : 2020/01/07
 * desc :
 * version: 1.0.0
 * </pre>
 */

@IntDef({
        ASKING_PERMISSIONS,
        GETTING_LOCATION_FROM_GOOGLE_PLAY_SERVICES,
        GETTING_LOCATION_FROM_GPS_PROVIDER,
        GETTING_LOCATION_FROM_NETWORK_PROVIDER,
        GETTING_LOCATION_FROM_CUSTOM_PROVIDER
})
@Retention(RetentionPolicy.SOURCE)
public @interface ProcessType {

    /**
     * This type will be emitted when application doesn't have required permissions yet,
     * and library starts the process to ask for them. If application already has the permissions,
     * this will not be emitted.
     */
    int ASKING_PERMISSIONS = 1;

    /**
     * This type will be emitted when GooglePlayServices is available on device and possible to ask for location update,
     * otherwise it will not be emitted.
     */
    int GETTING_LOCATION_FROM_GOOGLE_PLAY_SERVICES = 2;

    /**
     * This type will be emitted as soon as library asks Location update with GPS provider,
     * otherwise it will not be emitted.
     */
    int GETTING_LOCATION_FROM_GPS_PROVIDER = 3;

    /**
     * This type will be emitted as soon as library asks Location update with Network provider,
     * otherwise it will not be emitted.
     */
    int GETTING_LOCATION_FROM_NETWORK_PROVIDER = 4;

    /**
     * This type will never be emitted by the library.
     * It is defined in case of a custom Location Provider set to LocationManager.
     */
    int GETTING_LOCATION_FROM_CUSTOM_PROVIDER = 5;
}
