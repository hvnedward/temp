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
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.preference.Preference;

import com.android.car.settings.R;
import com.android.car.settings.common.FragmentController;
import com.android.car.settings.wifi.WifiBasePreferenceController;

import com.humaxdigital.automotive.settings.common.SubPreference;

/**
 * Controls preference when Wifi is in disabled or enabling state
 */
public class WifiStatusPreferenceController extends WifiBasePreferenceController<Preference> {
    private static final String TAG = WifiStatusPreferenceController.class.getSimpleName();

    public WifiStatusPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    protected Class<Preference> getPreferenceType() {
        return Preference.class;
    }

    @Override
    public void onWifiStateChanged(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                getPreference().setVisible(true);
                getPreference().setTitle(R.string.wifi_disabled);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                getPreference().setVisible(true);
                getPreference().setTitle(R.string.loading_wifi_list);
                break;
            default:
                getPreference().setVisible(false);
        }
    }
}
