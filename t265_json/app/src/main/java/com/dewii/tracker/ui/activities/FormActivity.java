package com.dewii.tracker.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.dewii.tracker.R;
import com.dewii.tracker.api.ApiConstants;
import com.dewii.tracker.api.Volley;
import com.dewii.tracker.database.Preferences;
import com.dewii.tracker.databinding.ActivityFormBinding;
import com.dewii.tracker.services.LocationUpdateService;
import com.dewii.tracker.ui.BaseActivity;
import com.dewii.tracker.utils.AppConstants;
import com.dewii.tracker.utils.AppUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

public class FormActivity extends BaseActivity {
    public static final String TAG = "FormActivity";

    private ActivityFormBinding binding;

    private double latitude = 0.0, longitude = 0.0;
    private float speed = 0, accuracy = 0;
    private boolean triggerTypeTime = false, triggerTypeDistance = false;
    private boolean autoSwitched = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_form);
        bindingCreated();
    }

    @Override
    public void prepareViews() {
        getSupportActionBar().setTitle(R.string.app_name);

        JSONObject savedObject = Preferences.getJsonObject(this, Preferences.LAST_TESTING_DATA);
        if (savedObject != null) {
            binding.btnClear.setVisibility(View.VISIBLE);

            binding.etMapName.setText(savedObject.optString(ApiConstants.RequestKeys.MAP_NAME));
            binding.etCreatedBy.setText(savedObject.optString(ApiConstants.RequestKeys.MAP_CREATED_BY));
            binding.etCallingNumber.setText(savedObject.optString(ApiConstants.RequestKeys.CALLING_NUMBER));
            binding.etCallDuration.setText(savedObject.optString(ApiConstants.RequestKeys.CALL_DURATION));
            binding.rbtnTypeTime.setChecked(savedObject.optBoolean(ApiConstants.RequestKeys.TIME_BASE_TRIGGER));
            binding.rbtnTypeDistance.setChecked(savedObject.optBoolean(ApiConstants.RequestKeys.DISTANCE_BASED_TRIGGER));
        } else
            binding.btnClear.setVisibility(View.GONE);

        binding.progressBar.hide();

        binding.rgTriggerType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbtn_type_time) {
                triggerTypeTime = true;
                triggerTypeDistance = false;
            } else if (checkedId == R.id.rbtn_type_distance) {
                triggerTypeTime = false;
                triggerTypeDistance = true;
            }
        });
    }

    @Override
    public void onBindingCreated() {
        super.onBindingCreated();

        if (AppUtils.checkPermissions(this, true)) {
            binding.switchLocation.setChecked(true);
            startLocationUpdate();
        }
    }

    @Override
    public void addViewListeners() {
        binding.switchLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (autoSwitched) {
                autoSwitched = false;
                return;
            }

            if (isChecked) {
                if (AppUtils.checkPermissions(this, true))
                    startLocationUpdate();
            } else
                LocationUpdateService.forceStopService();
        });
        binding.btnClear.setOnClickListener(v -> {
            Preferences.clearPreference(this);
            binding.btnClear.setVisibility(View.GONE);
        });
        binding.btnSubmit.setOnClickListener(v -> {
            String baseUrl = "http://"+AppUtils.getText(binding.etIpAddress)+":"+AppUtils.getText(binding.etPort);
            toast(baseUrl);
            if (isRequestValid())
                processRequest(baseUrl, createRequestJson());
        });
    }

    public boolean isRequestValid() {
        if (AppUtils.isInvalid(binding.etMapName)) {
            toast("Invalid map name");
            return false;
        } else if (AppUtils.isInvalid(binding.etCreatedBy)) {
            toast("Invalid user name");
            return false;
        } else if (AppUtils.isInvalid(binding.etCallingNumber)) {
            toast("Invalid phone number");
            return false;
        } else if (AppUtils.isInvalid(binding.etCallDuration)) {
            toast("Invalid calling duration");
            return false;
        } else if (!triggerTypeTime && !triggerTypeDistance) {
            toast("Invalid trigger type");
            return false;
        } else if (AppUtils.isInvalid(binding.etIpAddress)) {
            toast("Invalid destination IP");
            return false;
        } else if (AppUtils.isInvalid(binding.etPort)) {
            toast("Invalid destination PORT");
            return false;
        }
        return true;
    }

    private String getText(@NonNull EditText editText) {
        return editText.getText().toString().trim();
    }

    public JSONObject createRequestJson() {
        JSONObject requestObject = new JSONObject();

        try {
            requestObject.put(ApiConstants.RequestKeys.MAP_NAME, getText(binding.etMapName));
            requestObject.put(ApiConstants.RequestKeys.MAP_CREATED_BY, getText(binding.etCreatedBy));
            requestObject.put(ApiConstants.RequestKeys.CALLING_NUMBER, getText(binding.etCallingNumber));
            requestObject.put(ApiConstants.RequestKeys.CALL_DURATION, getText(binding.etCallDuration));

            JSONObject gpsObject = new JSONObject();
            gpsObject.put(ApiConstants.RequestKeys.LATITUDE, latitude);
            gpsObject.put(ApiConstants.RequestKeys.LONGITUDE, longitude);
            requestObject.put(ApiConstants.RequestKeys.LAST_LOCATION, gpsObject);

            requestObject.put(ApiConstants.RequestKeys.TIME_BASE_TRIGGER, triggerTypeTime);
            requestObject.put(ApiConstants.RequestKeys.DISTANCE_BASED_TRIGGER, triggerTypeDistance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestObject;
    }

    private void processRequest(String baseUrl, JSONObject jsonRequest) {
        binding.btnSubmit.setEnabled(false);
        binding.progressBar.show();

        volley().executePostJsonRequest(this, baseUrl, jsonRequest, new Volley.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                binding.btnSubmit.setEnabled(true);
                binding.progressBar.hide();

                Preferences.saveJsonObject(FormActivity.this, Preferences.LAST_TESTING_DATA, jsonRequest);
                toast(jsonRequest.toString());
            }

            @Override
            public void onError(VolleyError error) {
                binding.btnSubmit.setEnabled(true);
                binding.progressBar.hide();

                toast(error.getMessage());
            }
        });
    }

    @Override
    public void onLocationChanged(double latitude, double longitude, float speed, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accuracy = accuracy;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (AppUtils.checkPermission(this, AppConstants.USAGE_LOCATION)) {
            binding.switchLocation.setChecked(true);
            startLocationUpdate();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setCancelable(false)
                    .setTitle("Alas! Permission required.")
                    .setMessage("This permission is required by the application for better performance.")
                    .setPositiveButton("Allow", (dialog, which) -> {
                        Intent intentSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intentSettings.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intentSettings);
                    })
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            Log.e(TAG, "onActivityResult: Request denied! " + data);
            return;
        }

        if (requestCode == 0) {
            if (resultCode == RESULT_OK)
                startLocationUpdate();

        } else if (requestCode == AppConstants.USAGE_LOCATION) {
            if (resultCode == RESULT_OK)
                startLocationUpdate();

        }
    }
}
