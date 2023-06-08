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

package com.humaxdigital.automotive.settings.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.car.settings.R;
import com.android.car.settings.common.Logger;

import com.humaxdigital.automotive.settings.common.DualPaneSettingsActivity;
import com.humaxdigital.automotive.settings.common.PaneMenuSettingsFragment;
import com.humaxdigital.automotive.settings.datetime.DatetimeSettingsFragment;
import com.humaxdigital.automotive.settings.display.DisplaySettingsFragment;

public class HomeDualPaneSettingsActivity extends DualPaneSettingsActivity {
    private static final Logger LOG = new Logger(HomeDualPaneSettingsActivity.class);

    private PaneMenuSettingsFragment mPaneMenuSettingsFragment;

    private static final String KEY_HAS_NEW_INTENT =
            "com.android.car.settings.common.CarSettingActivity.KEY_HAS_NEW_INTENT";

    private boolean mHasNewIntent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mHasNewIntent = savedInstanceState.getBoolean(KEY_HAS_NEW_INTENT, mHasNewIntent);
        }

        initialize();
    }

    private void initialize() {
        // NOTE: Although SettingsProvider defaults.xml is re-defined,
        // we set here again when starting Settings.

//        if (Settings.Global.getInt(getContentResolver(),
//                Settings.Global.AUTO_TIME, 0) > 0) {
//            // if auto time is enabled, disable auto time. currently it is not supported
//
//            Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
//            sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
//        }
//
        if (Settings.Global.getInt(getContentResolver(),
                Settings.Global.AUTO_TIME_ZONE, 0) > 0) {
            // if auto time zone is enabled, disable auto time. currently it is not supported

            Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
            sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
        }

//        String dateFormat = Settings.System.getString(getContentResolver(),
//                Settings.System.DATE_FORMAT);
//
//        if (dateFormat == null || dateFormat.isEmpty()) {
//            Settings.System.putString(getContentResolver(), Settings.System.DATE_FORMAT,
//                    "MM/dd/yyyy");
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_NEW_INTENT, mHasNewIntent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LOG.d("onNewIntent" + intent);
        setIntent(intent);
        mHasNewIntent = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHasNewIntent) {
            Fragment fragment = getFragmentForIntent(/* context= */ this,
                    getIntent());
            launchIfDifferent(fragment);
            mHasNewIntent = false;
        }
    }

    @Nullable
    @Override
    protected PaneMenuSettingsFragment getMenuFragment() {
        if (mPaneMenuSettingsFragment == null) {
            mPaneMenuSettingsFragment = HomePaneMenuFragment.newInstance();
        }
        return mPaneMenuSettingsFragment;
    }

    /**
     * Gets the fragment to show onCreate. This will only be launched if it is different from the
     * current fragment shown.
     */
    @Override
    @Nullable
    protected Fragment getInitialSubFragment() {
        if (getCurrentFragment() != null) {
            LOG.d("get current fragment (" + getCurrentFragment().getClass().getName() + ")");
            return getCurrentFragment();
        }
        LOG.d("get initial sub fragment (" + getString(R.string.config_settings_initial_sub_fragment) + ")");
        return Fragment.instantiate(this,
                getString(R.string.config_settings_initial_sub_fragment));
    }

    @Nullable
    protected Fragment getFragmentForIntent(Context context, @Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        String action = intent.getAction();
        if (action == null) {
            return null;
        }
        switch (action) {
            case Settings.ACTION_SETTINGS:
                return new DisplaySettingsFragment();

            case Intent.ACTION_QUICK_CLOCK:
            case Settings.ACTION_DATE_SETTINGS:
                return new DatetimeSettingsFragment();

            default:
                return Fragment.instantiate(context,
                        context.getString(R.string.config_settings_initial_sub_fragment));
        }
    }
}
