package com.dewii.tracker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dewii.tracker.background.AppStateTask;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public final class AppUtils {
    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACCESS_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
    private static final String ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;

    public static final String[] PERMISSIONS = new String[]{
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            ACCESS_BACKGROUND_LOCATION,
            ACCESS_NETWORK_STATE
    };

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_BACKGROUND_LOCATION
    };
    private static final String[] NETWORK_PERMISSION = new String[]{ACCESS_NETWORK_STATE};

    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean isRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static boolean checkPermissions(Activity activity, boolean showDialog) {
        boolean allowedBackgroundLocation = Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                || hasPermission(activity, ACCESS_BACKGROUND_LOCATION);
        boolean allowedLocationAccess = hasPermission(activity, ACCESS_COARSE_LOCATION)
                && hasPermission(activity, ACCESS_FINE_LOCATION);
        boolean allowedNetworkAccess = hasPermission(activity, ACCESS_NETWORK_STATE);

        if (!allowedBackgroundLocation || !allowedLocationAccess || !allowedNetworkAccess) {
            try {
                if (showDialog) {
                    new MaterialAlertDialogBuilder(activity)
                            .setTitle("Usage Needed")
                            .setMessage("App uses these permissions to provide better experience.")
                            .setPositiveButton("Ok, Show Permissions",
                                    (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        ActivityCompat.requestPermissions(activity, PERMISSIONS, 0);
                                    }).create().show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else
            return true;
    }

    public static boolean checkPermission(Activity activity, int usage) {
        return checkPermission(activity, usage, true);
    }

    public static boolean checkPermission(Context context, int usage) {
        return checkPermission(context, usage, false);
    }

    private static boolean checkPermission(Context context, int usage, boolean showDialog) {
        String permission = PERMISSIONS[0];
        String[] permissions = PERMISSIONS;
        Runnable rationale = () -> {
        };

        if (usage == AppConstants.USAGE_LOCATION) {
            permission = ACCESS_COARSE_LOCATION;
            permissions = LOCATION_PERMISSIONS;
            rationale = () -> showLocationRationaleDialog(context);

        } else if (usage == AppConstants.USAGE_NETWORK_STATE) {
            permission = ACCESS_NETWORK_STATE;
            permissions = NETWORK_PERMISSION;
            rationale = () -> showNetworkStateRationaleDialog(context);

        }

        if (!hasPermission(context, permission)) {
            try {
                if (showDialog) {
                    if (isRationale((Activity) context, permission))
                        rationale.run();
                    else
                        ActivityCompat.requestPermissions((Activity) context, permissions, usage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else
            return true;
    }

    private static void showRationalePermissionDialog(Context context, String title, String message,
                                                      String[] permissions, int requestCode) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok, Allow", (dialogInterface, i)
                        -> ActivityCompat.requestPermissions((Activity) context, permissions, requestCode))
                .create().show();
    }

    private static void showLocationRationaleDialog(Context context) {
        showRationalePermissionDialog(context,
                "Location Permission Needed",
                "App accesses location to help navigate to vehicles on site.",
                LOCATION_PERMISSIONS,
                AppConstants.USAGE_LOCATION
        );
    }

    private static void showNetworkStateRationaleDialog(Context context) {
        showRationalePermissionDialog(context,
                "Network State Read Permission Needed",
                "App accesses network state to sync pending slips to server.",
                NETWORK_PERMISSION,
                AppConstants.USAGE_NETWORK_STATE
        );
    }

    public static boolean isAppInForeground(Context context) {
        try {
            return new AppStateTask().execute(context).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isInvalid(EditText editText) {
        return isInvalid(getText(editText));
    }

    public static boolean isInvalid(String s) {
        return TextUtils.isEmpty(s) || s.toLowerCase().equals("null") || s.toLowerCase().contains("n/a");
    }

    public static String getBase64(String value) {
        try {
            return Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void hideSoftKeyboard(Context context) {
        try{
            if (context != null) {
                if (((Activity) context).getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
