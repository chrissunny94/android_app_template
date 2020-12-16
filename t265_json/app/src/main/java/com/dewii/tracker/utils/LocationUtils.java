package com.dewii.tracker.utils;

import android.app.Activity;
import android.app.Service;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.concurrent.TimeUnit;

public final class LocationUtils {
    public static final String TAG = LocationUtils.class.getCanonicalName();

    public static final int REQUEST_CHECK_SETTINGS = 0xA010;
    public static long INTERVAL = TimeUnit.MINUTES.toMillis(1);
    public static long FASTEST_INTERVAL = INTERVAL / 6;

    private LocationUtils() {

    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(NumericUtils.deg2rad(lat1))
                * Math.sin(NumericUtils.deg2rad(lat2))
                + Math.cos(NumericUtils.deg2rad(lat1))
                * Math.cos(NumericUtils.deg2rad(lat2))
                * Math.cos(NumericUtils.deg2rad(theta));
        dist = Math.acos(dist);
        dist = NumericUtils.rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist / 1000);
    }

    public static void showEnableLocationDialog(Activity activity, Service service, LocationRequest locationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        LocationServices.getSettingsClient(service).checkLocationSettings(builder.build())
                .addOnCompleteListener(locationSettingsResponse -> {
                    try {
                        locationSettingsResponse.getResult(ApiException.class);
                    } catch (ApiException e) {
                        e.printStackTrace();

                        if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ((ResolvableApiException) e).startResolutionForResult(activity,
                                        LocationUtils.REQUEST_CHECK_SETTINGS);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
}
