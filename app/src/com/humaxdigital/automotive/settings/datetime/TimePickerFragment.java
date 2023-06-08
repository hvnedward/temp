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

package com.humaxdigital.automotive.settings.datetime;

import android.annotation.DrawableRes;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.android.car.settings.R;
import com.android.car.settings.datetime.DatetimeSettingsFragment;

import com.humaxdigital.automotive.settings.common.SubBaseFragment;

import java.util.Calendar;

/**
 * Sets the system time.
 */
public class TimePickerFragment extends SubBaseFragment {
    private static final int MILLIS_IN_SECOND = 1000;

    private TimePicker mTimePicker;
    private boolean mIs24Hour;

    @Override
    @LayoutRes
    protected int getActionBarLayoutId() {
        return R.layout.hmx_menu_action_bar;
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return mIs24Hour ? R.layout.hmx_24hour_time_picker : R.layout.hmx_time_picker;
    }

    @Override
    @StringRes
    protected int getTitleId() {
        return R.string.time_picker_title;
    }

    @Override
    @DrawableRes
    protected int getPaneImageResourceId() {
        return R.drawable.set_img_date;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mIs24Hour = DateFormat.is24HourFormat(getContext());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTimePicker = (TimePicker) getView().findViewById(R.id.time_picker);
        mTimePicker.setIs24HourView(mIs24Hour);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setTime();
            }
        });
    }

    private void setTime() {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
        c.set(Calendar.MINUTE, mTimePicker.getMinute());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = Math.max(c.getTimeInMillis(), DatetimeSettingsFragment.MIN_DATE);
        if (when / MILLIS_IN_SECOND < Integer.MAX_VALUE) {
            ((AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE)).setTime(when);
            getContext().sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
        }
    }
}
