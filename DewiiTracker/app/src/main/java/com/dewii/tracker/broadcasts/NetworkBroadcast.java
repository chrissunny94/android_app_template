package com.dewii.tracker.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.dewii.tracker.ui.interfaces.OnNetworkBroadcastListener;
import com.dewii.tracker.utils.AppConstants;

public class NetworkBroadcast extends BroadcastReceiver {
    public static final String TAG = NetworkBroadcast.class.getCanonicalName();

    private OnNetworkBroadcastListener onNetworkBroadcastListener;

    public NetworkBroadcast() {
    }

    public NetworkBroadcast(OnNetworkBroadcastListener onNetworkBroadcastListener) {
        this.onNetworkBroadcastListener = onNetworkBroadcastListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(AppConstants.BROADCAST_NETWORK_CONNECTIVITY_CHANGED)) {

            boolean networkAvailable = isConnected(context);

            if (onNetworkBroadcastListener != null)
                onNetworkBroadcastListener.onNetworkBroadcast(networkAvailable);
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
