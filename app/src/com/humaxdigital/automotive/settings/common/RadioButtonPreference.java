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

package com.humaxdigital.automotive.settings.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.car.settings.R;

public class RadioButtonPreference extends Preference {

    private boolean mSelected = false;

    public RadioButtonPreference(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public RadioButtonPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RadioButtonPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioButtonPreference(Context context) {
        this(context, null);
    }

    private void init() {
        setLayoutResource(R.layout.hmx_radio_button_preference);
        setIcon(R.drawable.set_btn_radio_selector);
    }

    @Override
    public final void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setSelected(mSelected);
    }

    public boolean getSelected() {
        return mSelected;
    }

    public void setSelected(boolean select) {
        mSelected = select;
        notifyChanged();
    }
}
