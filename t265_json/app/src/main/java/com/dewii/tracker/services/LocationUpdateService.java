package com.dewii.tracker.services;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.dewii.tracker.R;
import com.dewii.tracker.broadcasts.GpsBroadcast;
import com.dewii.tracker.broadcasts.NetworkBroadcast;
import com.dewii.tracker.ui.interfaces.OnGpsBroadcastListener;
import com.dewii.tracker.ui.interfaces.OnNetworkBroadcastListener;
import com.dewii.tracker.utils.AppConstants;
import com.dewii.tracker.utils.AppUtils;
import com.dewii.tracker.utils.DateUtils;
import com.dewii.tracker.utils.LocationUtils;
import com.dewii.tracker.utils.NotificationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

public class LocationUpdateService extends Service implements OnNetworkBroadcastListener, OnGpsBroadcastListener {
    public static final String TAG = LocationUpdateService.class.getCanonicalName();

    private static LocationUpdateService locationUpdateService;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private NetworkBroadcast networkBroadcast;
    private GpsBroadcast gpsBroadcast;

    private NotificationManager mNotificationManager;
    private Location lastLocation;

    private boolean networkAvailable = false;

    @Override
    public void onCreate() {
        super.onCreate();

        locationRequest = createLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        onLocationChanged(location);
                    }
                }
            }
        };

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(NotificationUtils.LOCATION_CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NotificationUtils.LOCATION_NOTIFICATION_ID, getNotification("Starting location updates"));

        networkBroadcast = new NetworkBroadcast(this);
        registerReceiver(networkBroadcast, new IntentFilter(AppConstants.BROADCAST_NETWORK_CONNECTIVITY_CHANGED));

        gpsBroadcast = new GpsBroadcast(this);
        registerReceiver(gpsBroadcast, new IntentFilter(AppConstants.BROADCAST_GPS_CONNECTIVITY_CHANGED));

        locationUpdateService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();

        return START_STICKY;
    }

    protected LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(LocationUtils.INTERVAL)
                .setFastestInterval(LocationUtils.FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onGpsBroadcast(boolean turnedOn) {

    }

    @Override
    public void onNetworkBroadcast(boolean networkAvailable) {

    }

    public static void showEnableLocationDialog(Activity activity) {
        LocationUtils.showEnableLocationDialog(activity, locationUpdateService, locationUpdateService.locationRequest);
    }

    private void startLocationUpdates() {
        if (AppUtils.checkPermission(this, AppConstants.USAGE_LOCATION)) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback, null);
        }
    }

    public static void getCurrentLocation(OnCurrentLocationReceiveListener locationReceiveListener) throws Exception {
        if (locationUpdateService == null)
            throw new Exception(TAG + " = Service instance is null!");

        if (ActivityCompat.checkSelfPermission(locationUpdateService,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(locationUpdateService,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationUpdateService.fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(locationReceiveListener::onLocationFetched);
    }

    public void onLocationChanged(Location location) {
        this.lastLocation = location;

        updateLocation();
        mNotificationManager.notify(NotificationUtils.LOCATION_NOTIFICATION_ID,
                getNotification(String.format(Locale.getDefault(),
                        "Device Location:- %.3f:%.3f @ %.2f kmph",
                        lastLocation.getLatitude(),
                        lastLocation.getLongitude(),
                        lastLocation.getSpeed()
                )));
    }

    private void updateLocation() {
        sendBroadcast(new Intent(AppConstants.BROADCAST_LOCATION_CHANGED)
                .putExtra("LATITUDE", lastLocation.getLatitude())
                .putExtra("LONGITUDE", lastLocation.getLongitude())
                .putExtra("SPEED", lastLocation.getSpeed())
                .putExtra("ACCURACY", lastLocation.getAccuracy()));
    }

    private Notification getNotification(String message) {
        return NotificationUtils.getNotification(this, NotificationUtils.LOCATION_CHANNEL_ID,
                "Last location: " + message, "" + DateUtils.now());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

//        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkBroadcast);
        unregisterReceiver(gpsBroadcast);

//        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    public void stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            stopForeground(true);

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mNotificationManager.cancel(NotificationUtils.LOCATION_NOTIFICATION_ID);

        locationUpdateService = null;
        stopSelf();
    }

    public static void forceStopService() {
        if (locationUpdateService != null)
            locationUpdateService.stopService();
    }

    public interface OnCurrentLocationReceiveListener {
        void onLocationFetched(Location location);
    }
}


