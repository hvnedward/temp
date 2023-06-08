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

package com.humaxdigital.automotive.settings.system;

import android.annotation.DrawableRes;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.android.car.settings.R;
import com.android.car.settings.common.FragmentController;
import com.android.car.settings.system.MasterClearFragment;

import com.humaxdigital.automotive.settings.common.SubBaseFragment;

/**
 * Shows options to reset network settings, reset app preferences, and factory reset the device.
 */
public class ResetOptionsFragment extends SubBaseFragment {

    @Override
    @LayoutRes
    protected int getActionBarLayoutId() {
        return R.layout.hmx_menu_action_bar;
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.hmx_reset_options;
    }

    @Override
    @StringRes
    protected int getTitleId() {
        return R.string.reset_options_title;
    }

    @Override
    @DrawableRes
    protected int getPaneImageResourceId() {
        return R.drawable.set_img_system;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.system_reset_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentController) requireActivity()).launchFragment(new MasterClearFragment());
            }
        });
    }
}
