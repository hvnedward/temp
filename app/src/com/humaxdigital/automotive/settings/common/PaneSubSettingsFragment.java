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

import androidx.annotation.LayoutRes;

import com.android.car.settings.common.SettingsFragment;

public abstract class PaneSubSettingsFragment extends SettingsFragment {
    private static final int INVALID_LAYOUT_ID = 0;

    @LayoutRes
    protected int getActionBarLayoutId() {
        // disable action bar layout
        return INVALID_LAYOUT_ID;
    }
}
