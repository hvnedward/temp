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
package com.humaxdigital.automotive.settings.display;

import androidx.annotation.XmlRes;

import com.android.car.settings.R;

import com.humaxdigital.automotive.settings.common.PaneSubSettingsFragment;

/**
 * Preference fragment to host Display related preferences.
 */
public class DisplaySettingsFragment extends PaneSubSettingsFragment {

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.hmx_display_settings_fragment;
    }
}
