package com.dewii.tracker.utils;

import android.location.LocationManager;

public final class AppConstants {
    private AppConstants() {

    }

    public static final String BROADCAST_NETWORK_CONNECTIVITY_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String BROADCAST_GPS_CONNECTIVITY_CHANGED = LocationManager.PROVIDERS_CHANGED_ACTION;
    public static final String BROADCAST_LOCATION_CHANGED = "com.dewii.tracker.LOCATION_CHANGED";

    public static final int TIMEOUT_READ = 3 * 1000;
    public static final int TIMEOUT_CONNECT = 3 * 1000;

    public static final int USAGE_LOCATION = 0x09;
    public static final int USAGE_CAMERA = 0x0A;
    public static final int USAGE_NETWORK_STATE = 0x0F;
}
