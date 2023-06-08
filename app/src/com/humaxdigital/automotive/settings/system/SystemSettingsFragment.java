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

package com.humaxdigital.automotive.settings.system;

import androidx.annotation.LayoutRes;

import com.android.car.settings.R;

import com.humaxdigital.automotive.settings.common.PaneSubSettingsFragment;

/**
 * Shows basic info about the system and provide some actions like update, reset etc.
 */
public class SystemSettingsFragment extends PaneSubSettingsFragment {

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.hmx_system_settings_fragment;
    }
}
