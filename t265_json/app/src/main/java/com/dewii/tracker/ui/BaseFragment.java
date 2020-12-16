package com.dewii.tracker.ui;

import android.os.Handler;
import android.view.View;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "BaseFragment";

    public Handler handler = new Handler();

    public void bindingCreated() {
        onBindingCreated();
    }

    public void onBindingCreated() {
        addViewListeners();
    }

    public void prepareViews() {

    }

    public void addViewListeners() {

    }

    @Override
    public void onClick(View v) {

    }
}