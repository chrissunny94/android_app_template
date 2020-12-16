package com.dewii.tracker.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.dewii.tracker.R;
import com.dewii.tracker.databinding.ActivitySplashBinding;
import com.dewii.tracker.ui.BaseActivity;

public class SplashActivity extends BaseActivity {
    public static final String TAG = "SplashActivity";

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        bindingCreated();
    }

    @Override
    public void onBindingCreated() {
        handler.postDelayed(() -> {
            startActivity(new Intent(this, FormActivity.class));
            finish();
        }, 2000);
    }
}
