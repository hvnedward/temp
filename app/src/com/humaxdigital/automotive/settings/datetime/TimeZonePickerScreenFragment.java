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

package com.humaxdigital.automotive.settings.datetime;


import android.annotation.DrawableRes;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.XmlRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.android.car.settings.R;

import com.humaxdigital.automotive.settings.common.RadioButtonPreference;
import com.humaxdigital.automotive.settings.common.SubSettingsFragment;

import java.util.TimeZone;

/**
 * Lists all time zone and its offset from GMT.
 */
public class TimeZonePickerScreenFragment extends SubSettingsFragment {
    private final static String TIMEZONE_PICKER_KEY = "timezone_picker_screen";

    @Override
    @LayoutRes
    protected int getActionBarLayoutId() {
        return R.layout.hmx_menu_action_bar;
    }

    @Override
    @DrawableRes
    protected int getPaneImageResourceId() {
        return R.drawable.set_img_date;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.hmx_timezone_picker_screen_fragment;
    }

    @Override
    public RecyclerView onCreateRecyclerView(
            LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RecyclerView recyclerView;
        recyclerView = (RecyclerView)inflater.inflate(R.layout.hmx_settings_scrollbar_recycler_view, parent, false);
        return recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // NOTE: When using recycler view with scrollbar, scrollToPreference is not working.
        //       It seems to be a bug in PagedRecyclerView. NestedRecyclerView doesn't support it.
        scrollToPreference(TimeZone.getDefault().getID());

        PreferenceGroup preferenceGroup
                = (PreferenceGroup) findPreference(TIMEZONE_PICKER_KEY);
        Preference preference
                = preferenceGroup.findPreference(TimeZone.getDefault().getID());
        if (preference != null) {
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference)preference;
            radioButtonPreference.setSelected(true);
        }
    }
}
