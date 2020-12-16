package com.dewii.tracker.broadcasts;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.dewii.tracker.ui.interfaces.OnGpsBroadcastListener;
import com.dewii.tracker.utils.AppConstants;

public class GpsBroadcast extends BroadcastReceiver {
    public static final String TAG = GpsBroadcast.class.getCanonicalName();

    private OnGpsBroadcastListener onGpsBroadcastListener;

    public GpsBroadcast() {

    }

    public GpsBroadcast(OnGpsBroadcastListener onGpsBroadcastListener) {
        this.onGpsBroadcastListener = onGpsBroadcastListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().matches(AppConstants.BROADCAST_GPS_CONNECTIVITY_CHANGED)) {
            Log.i(TAG, "onReceive: " + intent);

            onGpsBroadcastListener.onGpsBroadcast(isGpsEnabled(context));
        }
    }

    public static boolean isGpsEnabled(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        // Find out what the settings say about which providers are enabled
        //  String locationMode = "Settings.Secure.LOCATION_MODE_OFF";
        int mode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        return mode != Settings.Secure.LOCATION_MODE_OFF;
    }
}
