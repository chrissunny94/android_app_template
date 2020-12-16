package com.dewii.tracker.utils;

import android.app.Notification;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.dewii.tracker.R;

public final class NotificationUtils {
    public static final String TAG = NotificationUtils.class.getCanonicalName();

    public static final String LOCATION_CHANNEL_ID = "channel_location_sharing";
    public static final int LOCATION_NOTIFICATION_ID = 12345678;

    private NotificationUtils() {

    }

    public static Notification getNotification(Context context, String nId, String content, String ticker) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, nId)
                .setContentText(content)
                .setContentTitle(context.getString(R.string.app_name))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setOnlyAlertOnce(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationUtils.LOCATION_CHANNEL_ID);
        }

        return builder.build();
    }
}
