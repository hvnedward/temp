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
 * limitations under the License
 */
package com.humaxdigital.automotive.settings.datetime;

import android.annotation.DrawableRes;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.android.car.settings.R;
import com.android.car.settings.datetime.DatetimeSettingsFragment;

import com.humaxdigital.automotive.settings.common.SubBaseFragment;

import java.util.Calendar;

/**
 * Sets the system date.
 */
public class DatePickerFragment extends SubBaseFragment {
    private static final int MILLIS_IN_SECOND = 1000;

    private DatePicker mDatePicker;

    @Override
    @LayoutRes
    protected int getActionBarLayoutId() {
        return R.layout.hmx_menu_action_bar;
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.hmx_date_picker;
    }

    @Override
    @StringRes
    protected int getTitleId() {
        return R.string.date_picker_title;
    }

    @Override
    @DrawableRes
    protected int getPaneImageResourceId() {
        return R.drawable.set_img_date;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDatePicker = (DatePicker) getView().findViewById(R.id.date_picker);
        mDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setDate();
            }
        });
    }

    private void setDate() {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, mDatePicker.getYear());
        c.set(Calendar.MONTH, mDatePicker.getMonth());
        c.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        long when = Math.max(c.getTimeInMillis(), DatetimeSettingsFragment.MIN_DATE);
        if (when / MILLIS_IN_SECOND < Integer.MAX_VALUE) {
            ((AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE)).setTime(when);
            getContext().sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
        }
    }
}
