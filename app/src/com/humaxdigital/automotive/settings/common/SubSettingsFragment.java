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

package com.humaxdigital.automotive.settings.common;

import android.annotation.DrawableRes;

import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.android.car.settings.common.EditTextPreferenceDialogFragment;
import com.android.car.settings.common.PasswordEditTextPreference;
import com.android.car.settings.common.PasswordEditTextPreferenceDialogFragment;
import com.android.car.settings.common.SettingsFragment;
import com.android.car.settings.common.SettingsListPreferenceDialogFragment;
import com.android.car.settings.common.ValidatedEditTextPreference;
import com.android.car.settings.common.ValidatedEditTextPreferenceDialogFragment;

public abstract class SubSettingsFragment extends SettingsFragment {
    private static final int INVALID_RESOURCE_ID = 0;

    @DrawableRes
    protected int getPaneImageResourceId() {
        return INVALID_RESOURCE_ID;
    }
}
