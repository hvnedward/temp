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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.Logger;
import com.android.car.settings.common.PreferenceController;
import com.android.settingslib.wifi.AccessPoint;

/** Business logic relating to the security type and associated password. */
public class NetworkPasswordPreferenceController extends
        PreferenceController<NetworkNameRestrictedPasswordEditTextPreference> {

    public static final String ACTION_PASSWORD_CHANGE =
            "com.android.car.settings.wifi.PasswordChangeAction";
    /** Key used to store the name of the network. */
    public static final String KEY_NETWORK_PASSWORD = "network_password";

    private static final Logger LOG = new Logger(NetworkPasswordPreferenceController.class);

    private final BroadcastReceiver mNameChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mNetworkName = intent.getStringExtra(
                    NetworkNamePreferenceController.KEY_NETWORK_NAME);
            getPreference().setNetworkName(mNetworkName);
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

    private String mNetworkName;
    private int mSecurityType = AccessPoint.SECURITY_NONE;

    public NetworkPasswordPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    protected Class<NetworkNameRestrictedPasswordEditTextPreference> getPreferenceType() {
        return NetworkNameRestrictedPasswordEditTextPreference.class;
    }

    @Override
    protected void onStartInternal() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mNameChangeReceiver,
                new IntentFilter(NetworkNamePreferenceController.ACTION_NAME_CHANGE));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mSecurityChangeReceiver,
                new IntentFilter(NetworkSecurityPreferenceController.ACTION_SECURITY_CHANGE));
    }

    @Override
    protected void onStopInternal() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mNameChangeReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSecurityChangeReceiver);
    }

    @Override
    protected void updateState(NetworkNameRestrictedPasswordEditTextPreference preference) {
        preference.setVisible(mSecurityType != AccessPoint.SECURITY_NONE);
    }

    @Override
    protected boolean handlePreferenceChanged(
            NetworkNameRestrictedPasswordEditTextPreference preference, Object newValue) {
        String password = newValue.toString();
        Intent intent = new Intent(ACTION_PASSWORD_CHANGE);
        intent.putExtra(KEY_NETWORK_PASSWORD, password);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(intent);

        return true;
    }
}
