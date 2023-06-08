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
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.car.settings.R;

public class PaneSubPreference extends Preference {

    private String mValue;

    public PaneSubPreference(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PaneSubPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PaneSubPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.paneSubPreferenceStyle);
    }

    public PaneSubPreference(Context context) {
        this(context, null);
    }

    @Override
    public final void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView textValue = (TextView) holder.findViewById(R.id.value);
        if (textValue != null && mValue != null) {
            textValue.setText(mValue);
        }
        ImageView imageView = (ImageView) holder.findViewById(R.id.arrow);
        if (imageView != null) {
            imageView.setVisibility(isEnabled() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public CharSequence getValue() {
        return mValue;
    }

    public void setValue(CharSequence value) {
        mValue = value.toString();
        notifyChanged();
    }
}
