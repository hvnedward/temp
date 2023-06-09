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

package com.humaxdigital.automotive.settings.bluetooth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.car.settings.R;
import com.android.car.settings.common.Logger;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager.BluetoothManagerCallback;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import android.view.LayoutInflater;

/**
 * BluetoothUtils provides an interface to the preferences
 * related to Bluetooth.
 */
public final class BluetoothUtils {
    private static final Logger LOG = new Logger(BluetoothUtils.class);
    private static final String SHARED_PREFERENCES_NAME = "bluetooth_settings";

    private static final BluetoothManagerCallback mOnInitCallback = new BluetoothManagerCallback() {
        @Override
        public void onBluetoothManagerInitialized(Context appContext,
                LocalBluetoothManager bluetoothManager) {
            com.android.settingslib.bluetooth.BluetoothUtils.setErrorListener(
                    com.humaxdigital.automotive.settings.bluetooth.BluetoothUtils::showError);
        }
    };

    // If a device was picked from the device picker or was in discoverable mode
    // in the last 60 seconds, show the pairing dialogs in foreground instead
    // of raising notifications
    private static final int GRACE_PERIOD_TO_SHOW_DIALOGS_IN_FOREGROUND = 60 * 1000;

    private static final String KEY_LAST_SELECTED_DEVICE = "last_selected_device";

    private static final String KEY_LAST_SELECTED_DEVICE_TIME = "last_selected_device_time";

    private static final String KEY_DISCOVERABLE_END_TIMESTAMP = "discoverable_end_timestamp";

    private BluetoothUtils() {
    }

    static void showError(Context context, String name, int messageResId) {
        showError(context, name, messageResId, getLocalBtManager(context));
    }

    private static void showError(Context context, String name, int messageResId,
            LocalBluetoothManager manager) {
        String message = context.getString(messageResId, name);
        Context activity = manager.getForegroundActivity();
        if (manager.isForegroundActivity()) {
			/*
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.bluetooth_error_title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show();
                    */

		AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.hmx_bt_error_dialog, null);

		TextView mTitleView = view.findViewById(R.id.bt_error_title);
		TextView mDescriptionView = view.findViewById(R.id.bt_error_description);
		TextView mButtonView = view.findViewById(R.id.bt_error_ok);				


		 LOG.v("showError R.string.bluetooth_error_title : " +R.string.bluetooth_error_title+ "string :" + context.getString(R.string.bluetooth_error_title));
		 mTitleView.setText(context.getString(R.string.bluetooth_error_title));
		 mTitleView.setVisibility(View.GONE);
		 
		 mDescriptionView.setText(message);
		 mButtonView.setText(context.getString(android.R.string.ok));
		 
 		 mBuilder.setView(view);
		 AlertDialog mDialog = mBuilder.create();
		 mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		 
		 mButtonView.setOnClickListener(v -> {
			 mDialog.dismiss();
		 });
		 mDialog.show();
		 
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    static long getDiscoverableEndTimestamp(Context context) {
        return getSharedPreferences(context).getLong(
                KEY_DISCOVERABLE_END_TIMESTAMP, 0);
    }

    static boolean shouldShowDialogInForeground(Context context,
            String deviceAddress, String deviceName) {
        LocalBluetoothManager manager = getLocalBtManager(context);
        if (manager == null) {
            LOG.v("manager == null - do not show dialog.");
            return false;
        }

        // If Bluetooth Settings is visible
        if (manager.isForegroundActivity()) {
            return true;
        }

        // If in appliance mode, do not show dialog in foreground.
        if ((context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_TYPE_APPLIANCE) == Configuration.UI_MODE_TYPE_APPLIANCE) {
            LOG.v("in appliance mode - do not show dialog.");
            return false;
        }

        long currentTimeMillis = System.currentTimeMillis();
        SharedPreferences sharedPreferences = getSharedPreferences(context);

        // If the device was in discoverABLE mode recently
        long lastDiscoverableEndTime = sharedPreferences.getLong(
                KEY_DISCOVERABLE_END_TIMESTAMP, 0);
        if ((lastDiscoverableEndTime + GRACE_PERIOD_TO_SHOW_DIALOGS_IN_FOREGROUND)
                > currentTimeMillis) {
            return true;
        }

        // If the device was discoverING recently
        LocalBluetoothAdapter adapter = manager.getBluetoothAdapter();
        if (adapter != null) {
            if (adapter.isDiscovering()) {
                return true;
            }
            if ((adapter.getDiscoveryEndMillis() +
                    GRACE_PERIOD_TO_SHOW_DIALOGS_IN_FOREGROUND) > currentTimeMillis) {
                return true;
            }
        }

        // If the device was picked in the device picker recently
        if (deviceAddress != null) {
            String lastSelectedDevice = sharedPreferences.getString(
                    KEY_LAST_SELECTED_DEVICE, null);

            if (deviceAddress.equals(lastSelectedDevice)) {
                long lastDeviceSelectedTime = sharedPreferences.getLong(
                        KEY_LAST_SELECTED_DEVICE_TIME, 0);
                if ((lastDeviceSelectedTime + GRACE_PERIOD_TO_SHOW_DIALOGS_IN_FOREGROUND)
                        > currentTimeMillis) {
                    return true;
                }
            }
        }


        if (!TextUtils.isEmpty(deviceName)) {
            // If the device is a custom BT keyboard specifically for this device
            String packagedKeyboardName = context.getString(
                    com.android.internal.R.string.config_packagedKeyboardName);
            if (deviceName.equals(packagedKeyboardName)) {
                LOG.v("showing dialog for packaged keyboard");
                return true;
            }
        }

        LOG.v("Found no reason to show the dialog - do not show dialog.");
        return false;
    }

    static void persistSelectedDeviceInPicker(Context context, String deviceAddress) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_LAST_SELECTED_DEVICE, deviceAddress);
        editor.putLong(KEY_LAST_SELECTED_DEVICE_TIME, System.currentTimeMillis());
        editor.apply();
    }

    static void persistDiscoverableEndTimestamp(Context context, long endTimestamp) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(KEY_DISCOVERABLE_END_TIMESTAMP, endTimestamp);
        editor.apply();
    }

    public static LocalBluetoothManager getLocalBtManager(Context context) {
        return LocalBluetoothManager.getInstance(context, mOnInitCallback);
    }
}
