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

package com.humaxdigital.automotive.settings.example;

import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;
import android.provider.Settings;

import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.MasterSwitchPreference;
import com.android.car.settings.common.PreferenceController;
import com.android.car.settings.wifi.CarWifiManager;
import com.android.car.settings.wifi.WifiUtil;
import com.humaxdigital.automotive.settingslib.util.ExtraSettingsConstants;

/**
 * Controller which determines if the top level entry into Wi-Fi settings should be displayed
 * based on device capabilities.
 */
public class MyCustomEntryPreferenceController extends PreferenceController<MasterSwitchPreference> {

    public MyCustomEntryPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    protected Class<MasterSwitchPreference> getPreferenceType() {
        return MasterSwitchPreference.class;
    }

    @Override
    protected void onCreateInternal() {
        getPreference().setSwitchToggleListener((preference, isChecked) -> {
            int menuSet = Settings.Global.getInt(getContext().getContentResolver(),
                    ExtraSettingsConstants.DEMO_MENU_SET, 0);
            int checked = isChecked ? 1 : 0;
            if (menuSet != checked) {
                Settings.Global.putInt(getContext().getContentResolver(),
                        ExtraSettingsConstants.DEMO_MENU_SET, checked);
            }
        });
    }

    @Override
    protected int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    protected void updateState(MasterSwitchPreference preference) {
        int menuSet = Settings.Global.getInt(getContext().getContentResolver(),
                ExtraSettingsConstants.DEMO_MENU_SET, 0);
        preference.setSwitchChecked(menuSet != 0 ? true : false);
    }
}
