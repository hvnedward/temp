/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.humaxdigital.automotive.settings.wifi;

import android.annotation.DrawableRes;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.XmlRes;
import androidx.recyclerview.widget.RecyclerView;

import com.android.car.settings.R;
import com.android.car.settings.wifi.CarWifiManager;

import com.humaxdigital.automotive.settings.common.SubSettingsFragment;

/**
 * Main page to host Wifi related preferences.
 */
public class WifiSettingsFragment extends SubSettingsFragment
        implements CarWifiManager.Listener {

    private static final int SEARCHING_DELAY_MILLIS = 1700;

    private CarWifiManager mCarWifiManager;
    private ProgressBar mProgressBar;
    private View mWifiSwitch;

    @Override
    @LayoutRes
    protected int getActionBarLayoutId() {
        return R.layout.hmx_action_bar_with_toggle;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.hmx_wifi_list_fragment;
    }

    @Override
    @DrawableRes
    protected int getPaneImageResourceId() {
        return R.drawable.set_img_wifi;
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RecyclerView recyclerView;
        recyclerView = (RecyclerView)inflater.inflate(R.layout.hmx_settings_scrollbar_recycler_view, parent, false);
        return recyclerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCarWifiManager = new CarWifiManager(getContext());

        mProgressBar = requireActivity().findViewById(R.id.progress_bar);
        setupWifiSwitch();
    }

    @Override
    public void onStart() {
        super.onStart();
        mCarWifiManager.addListener(this);
        mCarWifiManager.start();
        onWifiStateChanged(mCarWifiManager.getWifiState());
    }

    @Override
    public void onStop() {
        super.onStop();
        mCarWifiManager.removeListener(this);
        mCarWifiManager.stop();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCarWifiManager.destroy();
    }

    @Override
    public void onAccessPointsChanged() {
//        mProgressBar.setVisibility(View.VISIBLE);
//        getView().postDelayed(() -> mProgressBar.setVisibility(View.GONE), SEARCHING_DELAY_MILLIS);
    }

    @Override
    public void onWifiStateChanged(int state) {
        mWifiSwitch.setSelected(mCarWifiManager.isWifiEnabled());
//        switch (state) {
//            case WifiManager.WIFI_STATE_ENABLING:
//                mProgressBar.setVisibility(View.VISIBLE);
//                break;
//            default:
//                mProgressBar.setVisibility(View.GONE);
//        }
    }

    private void setupWifiSwitch() {
        mWifiSwitch = getActivity().findViewById(R.id.master_switch);
        mWifiSwitch.setSelected(mCarWifiManager.isWifiEnabled());
        mWifiSwitch.setOnClickListener(v -> {
            boolean selected = !v.isSelected();
            v.setSelected(selected);
            if (selected != mCarWifiManager.isWifiEnabled()) {
                mCarWifiManager.setWifiEnabled(selected);
            }
        });

        TextView on = getActivity().findViewById(R.id.toggle_text1);
        on.setText("ON");
        TextView off = getActivity().findViewById(R.id.toggle_text2);
        off.setText("OFF");
    }
}
