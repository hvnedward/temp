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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R.attr;
import androidx.preference.R.id;

import com.android.car.settings.R;

public class DropDownPreference extends ListPreference {
    private final Context mContext;
    private final ArrayAdapter<String> mAdapter;
    private Spinner mSpinner;
    private final OnItemSelectedListener mItemSelectedListener;
    private final int mItemLayout;

    public DropDownPreference(Context context) {
        this(context, (AttributeSet)null);
    }

    public DropDownPreference(Context context, AttributeSet attrs) {
        this(context, attrs, attr.dropdownPreferenceStyle);
    }

    public DropDownPreference(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public DropDownPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.DropDownPreference, defStyleAttr, defStyleRes);
        mItemLayout = a.getResourceId(R.styleable.DropDownPreference_itemLayout, 0);
        a.recycle();

        mItemSelectedListener = new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (position >= 0) {
                    String value = getEntryValues()[position].toString();
                    if (!value.equals(getValue()) && callChangeListener(value)) {
                        setValue(value);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        mContext = context;
        mAdapter = createAdapter();
        updateEntries();
    }

    protected void onClick() {
        mSpinner.performClick();
    }

    public void setEntries(@NonNull CharSequence[] entries) {
        super.setEntries(entries);
        updateEntries();
    }

    protected ArrayAdapter<String> createAdapter() {
        return new ArrayAdapter<>(mContext, mItemLayout);
    }

    private void updateEntries() {
        mAdapter.clear();
        if (getEntries() != null) {
            CharSequence[] var1 = getEntries();

            for (CharSequence c : var1) {
                mAdapter.add(c.toString());
            }
        }

    }

    public void setValueIndex(int index) {
        setValue(getEntryValues()[index].toString());
    }

    protected void notifyChanged() {
        super.notifyChanged();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

    }

    public void onBindViewHolder(PreferenceViewHolder view) {
        mSpinner = (Spinner)view.itemView.findViewById(id.spinner);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(mItemSelectedListener);
        mSpinner.setSelection(findSpinnerIndexOfValue(getValue()));
        super.onBindViewHolder(view);
    }

    private int findSpinnerIndexOfValue(String value) {
        CharSequence[] entryValues = getEntryValues();
        if (value != null && entryValues != null) {
            for(int i = entryValues.length - 1; i >= 0; --i) {
                if (entryValues[i].equals(value)) {
                    return i;
                }
            }
        }

        return -1;
    }
}

