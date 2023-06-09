/*
 * Copyright (C) 2018 The Android Open Source Project
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

import android.car.drivingstate.CarUxRestrictions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.Logger;
import com.android.car.settings.common.PreferenceController;
import com.android.car.settings.wifi.WifiUtil;
import com.android.settingslib.wifi.AccessPoint;

import com.humaxdigital.automotive.settings.common.ButtonBarPreference;

/** Business logic relating to the security type and associated password. */
public class NetworkConnectPreferenceController extends
        PreferenceController<ButtonBarPreference> {

    private static final Logger LOG = new Logger(NetworkConnectPreferenceController.class);

    private final BroadcastReceiver mNameChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mNetworkName = intent.getStringExtra(
                    NetworkNamePreferenceController.KEY_NETWORK_NAME);
            refreshUi();
        }
    };

    private final BroadcastReceiver mSecurityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSecurityType = intent.getIntExtra(
                    NetworkSecurityPreferenceController.KEY_SECURITY_TYPE,
                    AccessPoint.SECURITY_NONE);
            refreshUi();
        }
    };

    private final BroadcastReceiver mPasswordChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPassword = intent.getStringExtra(
                    NetworkPasswordPreferenceController.KEY_NETWORK_PASSWORD);
            refreshUi();
        }
    };

    private String mNetworkName;
    private int mSecurityType = AccessPoint.SECURITY_NONE;
    private String mPassword;

    public NetworkConnectPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    protected Class<ButtonBarPreference> getPreferenceType() {
        return ButtonBarPreference.class;
    }

    @Override
    protected void onStartInternal() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mNameChangeReceiver,
                new IntentFilter(NetworkNamePreferenceController.ACTION_NAME_CHANGE));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mSecurityChangeReceiver,
                new IntentFilter(NetworkSecurityPreferenceController.ACTION_SECURITY_CHANGE));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mPasswordChangeReceiver,
                new IntentFilter(NetworkPasswordPreferenceController.ACTION_PASSWORD_CHANGE));
    }

    @Override
    protected void onStopInternal() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mNameChangeReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSecurityChangeReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mPasswordChangeReceiver);
    }

    @Override
    protected void updateState(ButtonBarPreference preference) {
        preference.setEnabled(isConnectionAvailable());
    }

    private boolean isConnectionAvailable() {
        boolean available = false;

        if (!TextUtils.isEmpty(mNetworkName)) {
            if (mSecurityType == AccessPoint.SECURITY_NONE) {
                available = true;
            }
            else if (!TextUtils.isEmpty(mPassword)) {
                available = true;
            }
        }
        return available;
    }

    @Override
    protected boolean handlePreferenceChanged(
            ButtonBarPreference preference, Object newValue) {
        return true;
    }

    @Override
    protected boolean handlePreferenceClicked(ButtonBarPreference preference) {
        int netId = WifiUtil.connectToAccessPoint(getContext(), mNetworkName, mSecurityType,
                mPassword, /* hidden= */ true);

        LOG.d("connected to netId: " + netId);
        if (netId != WifiUtil.INVALID_NET_ID) {
            getFragmentController().goBack();
        }
        return true;
    }
}
