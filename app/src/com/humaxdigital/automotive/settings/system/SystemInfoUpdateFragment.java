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
package com.humaxdigital.automotive.settings.system;

import android.annotation.DrawableRes;
import android.car.userlib.CarUserManagerHelper;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.android.car.settings.R;
import com.android.car.settings.development.DevelopmentSettingsUtil;

import com.humaxdigital.automotive.settings.common.SubBaseFragment;

import java.util.List;

/**
 * Sets the system date.
 */
public class SystemInfoUpdateFragment extends SubBaseFragment {
    private static final String TAG = SystemInfoUpdateFragment.class.getSimpleName();

    private static final String SYSTEM_UPDATE_ACTION = "android.settings.SYSTEM_UPDATE_SETTINGS";
    private boolean mActivityFound;
    private Intent mIntent;

    private CarUserManagerHelper mCarUserManagerHelper;
    private Toast mDevHitToast;
    private int mDevHitCountdown;

    @Override
    @LayoutRes
    protected int getActionBarLayoutId() {
        return R.layout.hmx_menu_action_bar;
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.hmx_system_info_update;
    }

    @Override
    @StringRes
    protected int getTitleId() {
        return R.string.system_system_info_entry;
    }

    @Override
    @DrawableRes
    protected int getPaneImageResourceId() {
        return R.drawable.set_img_system;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCarUserManagerHelper = new CarUserManagerHelper(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        mDevHitToast = null;
        mDevHitCountdown = DevelopmentSettingsUtil.isDevelopmentSettingsEnabled(getContext(),
                mCarUserManagerHelper) ? -1 : getTapsToBecomeDeveloper();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView version = view.findViewById(R.id.software_version);
        String versionString = "SW Version " + Build.VERSION.RELEASE;   // Build.DISPLAY
        version.setText(versionString);
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuildVersionClicked();
            }
        });

        Button button = view.findViewById(R.id.software_update_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivityFound) {
                    getContext().startActivity(mIntent);
                }
            }
        });

        // check update activity
        checkUpdateActivity();
    }

    private void checkUpdateActivity() {
        Intent intent = new Intent(SYSTEM_UPDATE_ACTION);
        // Find the activity that is in the system image.
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = list.get(i);
            if ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)
                    != 0) {
                Log.i(TAG, "Found update activity : " + resolveInfo.activityInfo.packageName
                        + ", " + resolveInfo.activityInfo.name);

                // Replace the intent with this specific activity.
                mIntent = new Intent().setClassName(resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                mActivityFound = true;
            }
        }

        if (!mActivityFound) {
            Log.i(TAG, "No update activity found.");
        }
    }

    protected boolean onBuildVersionClicked() {
        if (!mCarUserManagerHelper.isCurrentProcessAdminUser()
                && !mCarUserManagerHelper.isCurrentProcessDemoUser()) {
            Log.i(TAG, "Version clicked, but not the admin user.");
            return false;
        }

        if (!DevelopmentSettingsUtil.isDeviceProvisioned(getContext())) {
            Log.i(TAG, "Version clicked, but device is not provisioned");
            return false;
        }

        if (mDevHitCountdown > 0) {
            mDevHitCountdown--;
            if (mDevHitCountdown == 0) {
                DevelopmentSettingsUtil.setDevelopmentSettingsEnabled(getContext(), true);
                showToast(getContext().getString(R.string.show_dev_on), Toast.LENGTH_LONG);
            } else if (mDevHitCountdown <= getTapsToBecomeDeveloper() - getTapsToShowToast()) {
                showToast(getContext().getResources().getQuantityString(
                        R.plurals.show_dev_countdown, mDevHitCountdown, mDevHitCountdown),
                        Toast.LENGTH_SHORT);
            }
        } else if (mDevHitCountdown < 0) {
            showToast(getContext().getString(R.string.show_dev_already), Toast.LENGTH_LONG);
        }
        return true;
    }

    private void showToast(String text, @Toast.Duration int duration) {
        if (mDevHitToast != null) {
            mDevHitToast.cancel();
        }
        mDevHitToast = Toast.makeText(getContext(), text, duration);
        mDevHitToast.show();
    }

    private int getTapsToBecomeDeveloper() {
        return getContext().getResources().getInteger(
                R.integer.enable_developer_settings_click_count);
    }

    private int getTapsToShowToast() {
        return getContext().getResources().getInteger(
                R.integer.enable_developer_settings_clicks_to_show_toast_count);
    }
}
