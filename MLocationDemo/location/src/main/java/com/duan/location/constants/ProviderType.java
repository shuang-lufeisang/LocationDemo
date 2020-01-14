package com.duan.location.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.duan.location.constants.ProviderType.DEFAULT_PROVIDERS;
import static com.duan.location.constants.ProviderType.GOOGLE_PLAY_SERVICES;
import static com.duan.location.constants.ProviderType.GPS;
import static com.duan.location.constants.ProviderType.NETWORK;
import static com.duan.location.constants.ProviderType.NONE;

@IntDef({NONE, GOOGLE_PLAY_SERVICES, GPS, NETWORK, DEFAULT_PROVIDERS})
@Retention(RetentionPolicy.SOURCE)
public @interface ProviderType {

    int NONE = 0;
    int GOOGLE_PLAY_SERVICES = 1;
    int GPS = 2;
    int NETWORK = 3;
    int DEFAULT_PROVIDERS = 4; // Covers both GPS and NETWORK
}
