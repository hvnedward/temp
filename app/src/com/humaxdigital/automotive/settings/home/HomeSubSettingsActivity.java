/*
 * Copyright (C) 2022 The Android Open Source Project
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
 * limitations under the License
 */

package com.humaxdigital.automotive.settings.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.car.settings.R;
import com.android.car.settings.common.Logger;

import com.humaxdigital.automotive.settings.bluetooth.BluetoothSettingsFragment;
import com.humaxdigital.automotive.settings.common.SubSettingsActivity;
import com.humaxdigital.automotive.settings.datetime.DatePickerFragment;
import com.humaxdigital.automotive.settings.datetime.TimePickerFragment;
import com.humaxdigital.automotive.settings.datetime.TimeZonePickerScreenFragment;
import com.humaxdigital.automotive.settings.storage.StorageSettingsFragment;
import com.humaxdigital.automotive.settings.system.ResetOptionsFragment;
import com.humaxdigital.automotive.settings.system.SystemInfoUpdateFragment;
import com.humaxdigital.automotive.settings.utils.SettingsConstants;
import com.humaxdigital.automotive.settings.wifi.WifiSettingsFragment;

/**
 * Root activity used for most of the Settings app. This activity provides additional functionality
 * which handles intents.
 */
public class HomeSubSettingsActivity extends SubSettingsActivity {

    private static final Logger LOG = new Logger(HomeSubSettingsActivity.class);

    private static final String KEY_HAS_NEW_INTENT =
            "com.android.car.settings.common.CarSettingActivity.KEY_HAS_NEW_INTENT";

    private boolean mHasNewIntent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOG.d("onCreate() is called");

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mHasNewIntent = savedInstanceState.getBoolean(KEY_HAS_NEW_INTENT, mHasNewIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_NEW_INTENT, mHasNewIntent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LOG.d("onNewIntent" + intent);
        setIntent(intent);
        mHasNewIntent = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHasNewIntent) {
            Fragment fragment = getFragmentForIntent(/* context= */ this,
                    getIntent());
            launchIfDifferent(fragment);
            mHasNewIntent = false;
        }
    }

    @Override
    @Nullable
    protected Fragment getInitialFragment() {
        return null;
    }

    @Nullable
    protected Fragment getFragmentForIntent(Context context, @Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        String action = intent.getAction();
        if (action == null) {
            return null;
        }
        switch (action) {
            case SettingsConstants.ACTION_WIFI_SETTINGS:
                return new WifiSettingsFragment();

            case SettingsConstants.ACTION_BLUETOOTH_SETTINGS:
                return new BluetoothSettingsFragment();

            case SettingsConstants.ACTION_ZONE_PICKER:
                return new TimeZonePickerScreenFragment();

            case SettingsConstants.ACTION_STORAGE_SETTINGS:
                return new StorageSettingsFragment();

            default:
                return null;
        }
    }
}
