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

package com.humaxdigital.automotive.settings.wifi.details;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.car.settings.R;
import com.android.car.settings.wifi.details.WifiDetailsPreference;

/**
 * A Preference to be used with the Wifi Network Detail Fragment that allows a summary text to be
 * set inside the widget resource
 */
public class WifiDetailsSummaryPreference extends WifiDetailsPreference {
    public WifiDetailsSummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.hmx_summary_preference);
        setWidgetLayoutResource(R.layout.hmx_summary_preference_widget);
    }
}
