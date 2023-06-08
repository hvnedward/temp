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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.car.settings.R;

public class EditInputTextPreference extends Preference {

    public EditInputTextPreference(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setLayoutResource(R.layout.hmx_edit_input_text_preference);
    }

    public EditInputTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditInputTextPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditInputTextPreference(Context context) {
        this(context, null);
    }

    @Override
    public final void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        EditText editText = (EditText)holder.findViewById(R.id.value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                callChangeListener(editText.getText());
            }
        });
    }
}
