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
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.car.settings.R;
import com.android.car.settings.common.TwoActionPreference;

/** A preference that can be clicked on one side and toggled on another. */
public class ToggleSwitchPreference extends TwoActionPreference {

    /**
     * Interface definition for a callback to be invoked when the switch is toggled.
     */
    public interface OnSwitchToggleListener {
        /**
         * Called when a switch was toggled.
         *
         * @param preference the preference whose switch was toggled.
         * @param isChecked  the new state of the switch.
         */
        void onToggle(ToggleSwitchPreference preference, boolean isChecked);
    }

    private View mSwitch;
    private boolean mIsChecked;
    private OnSwitchToggleListener mToggleListener;

    private String mToggleText1;
    private String mToggleText2;

    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener =
            (buttonView, isChecked) -> {
                if (mToggleListener != null) {
                    mToggleListener.onToggle(this, isChecked);
                }
            };

    public ToggleSwitchPreference(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ToggleSwitchPreference, defStyleAttr, defStyleRes);

        mToggleText1 = a.getString(R.styleable.ToggleSwitchPreference_toggleText1);
        mToggleText2 = a.getString(R.styleable.ToggleSwitchPreference_toggleText2);

        a.recycle();

        init();
    }

    public ToggleSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ToggleSwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toggleSwitchPreferenceStyle);
    }

    public ToggleSwitchPreference(Context context) {
        this(context, null);
    }

    private void init() {
        setLayoutResource(R.layout.hmx_toggle_switch_preference);
        setWidgetLayoutResource(R.layout.hmx_toggle_switch_widget);
    }

    /** Sets the listener that handles the change in switch state. */
    public void setSwitchToggleListener(OnSwitchToggleListener listener) {
        mToggleListener = listener;
    }

    /** Gets the listener that handles the change in switch state. */
    public OnSwitchToggleListener getSwitchToggleListener() {
        return mToggleListener;
    }

    @Override
    protected void onBindWidgetFrame(View widgetFrame) {
        mSwitch = widgetFrame.findViewById(R.id.master_switch);
        mSwitch.setSelected(mIsChecked);
        widgetFrame.setOnClickListener(v -> {
            setSwitchChecked(!mIsChecked);
            callChangeListener(mIsChecked);
        });

        TextView text1 = widgetFrame.findViewById((R.id.toggle_text1));
        if (text1 != null && mToggleText1 != null) {
            text1.setText(mToggleText1);
        }

        TextView text2 = widgetFrame.findViewById((R.id.toggle_text2));
        if (text2 != null && mToggleText2 != null) {
            text2.setText(mToggleText2);
        }
    }

    /**
     * Sets the state of the switch. Can be set even when it isn't visible or bound in order to set
     * the initial state.
     */
    public void setSwitchChecked(boolean checked) {
        mIsChecked = checked;
        if (!isActionShown()) {
            return;
        }
        if (mSwitch != null) {
            mSwitch.setSelected(checked);
        }
    }

    /** Gets the state of the switch. */
    public boolean isSwitchChecked() {
        return mIsChecked;
    }
}
