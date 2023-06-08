/*
 * Copyright (C) 2021 The Android Open Source Project
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.humaxdigital.automotive.settings.common.DualPaneSettingsActivity;
import com.humaxdigital.automotive.settings.common.PaneMenuSettingsFragment;

public class DemoDualPaneSettingsActivity extends DualPaneSettingsActivity {
    private PaneMenuSettingsFragment mPaneMenuSettingsFragment;

    private static final String defaultSubFragmentClass =
//            "com.android.car.settings.display.DisplaySettingsFragment";
            "com.android.car.settings.sound.SoundSettingsFragment";

    @Nullable
    @Override
    protected PaneMenuSettingsFragment getMenuFragment() {
        if (mPaneMenuSettingsFragment == null) {
            mPaneMenuSettingsFragment = DemoPaneMenuFragment.newInstance();
        }
        return mPaneMenuSettingsFragment;
    }

    @Override
    @Nullable
    protected Fragment getInitialSubFragment() {
        if (getCurrentFragment() != null) {
            return getCurrentFragment();
        }
        return Fragment.instantiate(this,
                defaultSubFragmentClass);
    }
}
