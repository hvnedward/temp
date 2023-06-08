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

import android.car.drivingstate.CarUxRestrictions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.PreferenceController;

import com.humaxdigital.automotive.settings.common.DropDownPreference;
import com.humaxdigital.automotive.settings.common.ToggleSwitchPreference;

import java.util.Calendar;

/**
 * Business logic for toggle which chooses between 12 hour or 24 hour formats.
 */
public class DateFormatPreferenceController extends PreferenceController<DropDownPreference> {
    private static final String TAG = DateFormatPreferenceController.class.getSimpleName();

    private final BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshUi();
        }
    };

    public DateFormatPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    protected Class<DropDownPreference> getPreferenceType() {
        return DropDownPreference.class;
    }

    /** Starts the broadcast receiver which listens for time changes */
    @Override
    protected void onStartInternal() {
        // Listens to ACTION_TIME_CHANGED because the description needs to be changed based on
        // the ACTION_TIME_CHANGED intent that this toggle sends.
        getContext().registerReceiver(mTimeChangeReceiver,
                new IntentFilter(Intent.ACTION_TIME_CHANGED));
    }

    /** Stops the broadcast receiver which listens for time changes */
    @Override
    protected void onStopInternal() {
        getContext().unregisterReceiver(mTimeChangeReceiver);
    }

    @Override
    protected void updateState(DropDownPreference preference) {
        Calendar now = Calendar.getInstance();
        String dateFormat = Settings.System.getString(getContext().getContentResolver(),
                Settings.System.DATE_FORMAT);

        if (dateFormat.equals("MM/dd/yyyy")) {
            preference.setValueIndex(0);
        }
        else {
            preference.setValueIndex(1);
        }

        Log.i(TAG, "updateState :" + dateFormat);
    }

    @Override
    protected boolean handlePreferenceChanged(DropDownPreference preference, Object newValue) {
        String value = (String)newValue;

        Log.i(TAG, "handlePreferenceChanged:" + value);

        if (value.equals("0")) {
            Settings.System.putString(getContext().getContentResolver(),
                    Settings.System.DATE_FORMAT, "MM/dd/yyyy");
        }
        else {
            Settings.System.putString(getContext().getContentResolver(),
                    Settings.System.DATE_FORMAT, "yyyy/MM/dd");
        }

        getContext().sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));

        return true;
    }
}
