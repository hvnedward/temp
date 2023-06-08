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
package com.humaxdigital.automotive.settings.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.car.settings.R;
import com.android.car.settings.accounts.AccountSettingsFragment;
import com.android.car.settings.accounts.ChooseAccountFragment;
import com.android.car.settings.applications.ApplicationDetailsFragment;
import com.android.car.settings.applications.ApplicationsSettingsFragment;
import com.android.car.settings.applications.defaultapps.DefaultAutofillPickerFragment;
import com.android.car.settings.applications.specialaccess.ModifySystemSettingsFragment;
import com.android.car.settings.applications.specialaccess.NotificationAccessFragment;
import com.android.car.settings.applications.specialaccess.UsageAccessFragment;
import com.android.car.settings.bluetooth.BluetoothSettingsFragment;
import com.android.car.settings.datausage.DataUsageFragment;
import com.android.car.settings.datetime.DatetimeSettingsFragment;
import com.android.car.settings.display.DisplaySettingsFragment;
import com.android.car.settings.home.HomepageFragment;
import com.android.car.settings.inputmethod.KeyboardFragment;
import com.android.car.settings.language.LanguagePickerFragment;
import com.android.car.settings.location.LocationScanningFragment;
import com.android.car.settings.network.MobileNetworkFragment;
import com.android.car.settings.network.NetworkAndInternetFragment;
import com.android.car.settings.quicksettings.QuickSettingFragment;
import com.android.car.settings.sound.SoundSettingsFragment;
import com.android.car.settings.storage.StorageSettingsFragment;
import com.android.car.settings.system.AboutSettingsFragment;
import com.android.car.settings.users.UsersListFragment;
import com.android.car.settings.wifi.WifiSettingsFragment;
import com.android.car.settings.wifi.preferences.WifiPreferencesFragment;
import com.android.car.settings.common.Logger;

import com.humaxdigital.automotive.settingslib.util.ExtraSettingsConstants;


/**
 * Maps an Action string to a {@link Fragment} that can handle this Action.
 */
public class DemoFragmentResolver {
    private static final Logger LOG = new Logger(DemoFragmentResolver.class);

    private DemoFragmentResolver() {
    }

    /**
     * Returns a {@link Fragment} that can handle the given action, returns {@code null} if no
     * {@link Fragment} that can handle this {@link Intent} can be found. Keep the order of intent
     * actions same as in the manifest.
     */
    @Nullable
    public static Fragment getFragmentForIntent(Context context, @Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        String action = intent.getAction();
        if (action == null) {
            return null;
        }
        switch (action) {
            case Settings.ACTION_SETTINGS:
                return new DemoFragment();
            default:
                return Fragment.instantiate(context,
                        context.getString(R.string.config_settings_hierarchy_root_fragment));
        }
    }
}
