package com.dewii.tracker.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dewii.tracker.api.Volley;
import com.dewii.tracker.broadcasts.GpsBroadcast;
import com.dewii.tracker.broadcasts.NetworkBroadcast;
import com.dewii.tracker.services.LocationUpdateService;
import com.dewii.tracker.ui.interfaces.OnGpsBroadcastListener;
import com.dewii.tracker.ui.interfaces.OnNetworkBroadcastListener;
import com.dewii.tracker.utils.AppConstants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener,
        OnNetworkBroadcastListener, OnGpsBroadcastListener {
    public static final String TAG = "BaseActivity";

    public Handler handler = new Handler();

    private NetworkBroadcast networkBroadcastReceiver;
    private GpsBroadcast gpsBroadcastReceiver;
    private BroadcastReceiver locationBroadcastReceiver;

    private boolean networkAvailable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkBroadcastReceiver = new NetworkBroadcast(this);
        gpsBroadcastReceiver = new GpsBroadcast(this);
        locationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null &&
                        intent.getAction().equals(AppConstants.BROADCAST_LOCATION_CHANGED)) {
                    onLocationChanged(
                            intent.getDoubleExtra("LATITUDE", 0.0),
                            intent.getDoubleExtra("LONGITUDE", 0.0),
                            intent.getFloatExtra("SPEED", 0f),
                            intent.getFloatExtra("ACCURACY", 0f));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(networkBroadcastReceiver, new IntentFilter(AppConstants.BROADCAST_NETWORK_CONNECTIVITY_CHANGED));
        registerReceiver(gpsBroadcastReceiver, new IntentFilter(AppConstants.BROADCAST_GPS_CONNECTIVITY_CHANGED));
        registerReceiver(locationBroadcastReceiver, new IntentFilter(AppConstants.BROADCAST_LOCATION_CHANGED));
    }

    @Override
    public void onNetworkBroadcast(boolean networkAvailable) {
        this.networkAvailable = networkAvailable;
    }

    @Override
    public void onGpsBroadcast(boolean turnedOn) {
        if (!turnedOn) {
            new MaterialAlertDialogBuilder(this)
                    .setCancelable(false)
                    .setTitle("GPS is turned-off!")
                    .setMessage("Please turn on your gps to keep updating your location.")
                    .setPositiveButton("Allow", (dialog, which) -> {
                        dialog.dismiss();
                        LocationUpdateService.showEnableLocationDialog(this);
                    })
                    .show();
        }
    }

    public void startLocationUpdate() {
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else
            startService(serviceIntent);
    }

    public void stopLocationUpdate() {
        LocationUpdateService.forceStopService();
    }

    public void onLocationChanged(double latitude, double longitude, float speed, float accuracy) {

    }

    public void bindingCreated() {
        onBindingCreated();
    }

    public void onBindingCreated() {
        prepareViews();
        addViewListeners();
    }

    public void prepareViews() {

    }

    public void addViewListeners() {

    }

    @Override
    public void onClick(View v) {

    }

    public Volley volley() {
        return Volley.getInstance(this);
    }

    public void toast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(networkBroadcastReceiver);
        unregisterReceiver(gpsBroadcastReceiver);
        unregisterReceiver(locationBroadcastReceiver);
    }
}