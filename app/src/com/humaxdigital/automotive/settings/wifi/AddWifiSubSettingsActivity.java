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

package com.humaxdigital.automotive.settings.wifi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.car.settings.R;
import com.android.car.settings.common.Logger;

import com.humaxdigital.automotive.settings.common.SubSettingsActivity;

/**
 * Root activity used for most of the Settings app. This activity provides additional functionality
 * which handles intents.
 */
public class AddWifiSubSettingsActivity extends SubSettingsActivity {

    private static final Logger LOG = new Logger(AddWifiSubSettingsActivity.class);

    @Override
    @LayoutRes
    protected int getLayoutResourceId() {
        return R.layout.hmx_add_wifi_sub_settings_activity;
    }

    @Override
    @Nullable
    protected Fragment getInitialFragment() {
        return new AddWifiFragment();
    }
}
