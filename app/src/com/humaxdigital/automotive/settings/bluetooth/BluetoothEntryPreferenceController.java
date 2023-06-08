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

package com.humaxdigital.automotive.settings.bluetooth;

import static android.content.pm.PackageManager.FEATURE_BLUETOOTH;
import static android.os.UserManager.DISALLOW_BLUETOOTH;

import android.car.drivingstate.CarUxRestrictions;
import android.car.userlib.CarUserManagerHelper;
import android.content.Context;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.car.settings.R;


import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.PreferenceController;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;


import android.bluetooth.BluetoothAdapter; 
import com.android.settingslib.bluetooth.BluetoothCallback;



import com.humaxdigital.automotive.settings.common.PaneSubPreference;


import com.android.car.settings.common.Logger;
import android.os.Handler;
import android.os.HandlerThread;



import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller which determines if the top level entry into Bluetooth settings should be displayed
 * based on device capabilities and user restrictions.
 */
public class BluetoothEntryPreferenceController extends PreferenceController<PaneSubPreference> {

	private static final Logger LOG = new Logger(BluetoothEntryPreferenceController.class);

    private final CarUserManagerHelper mCarUserManagerHelper;
    private final LocalBluetoothManager mBluetoothManager;
	private LocalBluetoothAdapter mBluetoothAdapter = null;

	private Handler mHandler;
	private Runnable mRunable;


	private final BluetoothCallback mCallback = new BluetoothCallback() {
			@Override
			public void	onBluetoothStateChanged(int bluetoothState) {
				LOG.d("onBluetoothStateChanged :" + bluetoothState);
				refreshUi();			
			}
	
			@Override
			public void onConnectionStateChanged(CachedBluetoothDevice cachedDevice, int state) {
				LOG.d("onConnectionStateChanged :" + cachedDevice.getName() + "state : "+ state);
				refreshUi();
			}

			@Override
			public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedDevice,
					int state, int bluetoothProfile) {
				LOG.d("onProfileConnectionStateChanged :" + cachedDevice.getName() + "state : "+ state + "bluetoothProfile : " + bluetoothProfile);
				refreshUi();
			}
		};


	private class delayedRefreshUi implements Runnable {
		@Override
		public void run() {
			refreshUi();		
		}
	}


    public BluetoothEntryPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
        mCarUserManagerHelper = new CarUserManagerHelper(context);
        mBluetoothManager = BluetoothUtils.getLocalBtManager(context);
		mHandler = new Handler();
		mRunable = new delayedRefreshUi();

		if(mBluetoothManager != null)
		{
			mBluetoothManager.getEventManager().registerCallback(mCallback);
			mBluetoothAdapter = mBluetoothManager.getBluetoothAdapter();
			Collection<CachedBluetoothDevice> cachedDevices =
                mBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();
		}
		else
		{
			LOG.e("[updateState] mBluetoothManager is Null");
		}

		// Monitor action_state_changed
		//refreshUi();		
		mHandler.postDelayed(mRunable, 100);
    }

    @Override
    protected Class<PaneSubPreference> getPreferenceType() {
        return PaneSubPreference.class;
    }

    @Override
    public int getAvailabilityStatus() {
        if (!getContext().getPackageManager().hasSystemFeature(FEATURE_BLUETOOTH)) {
            return UNSUPPORTED_ON_DEVICE;
        }
        return mCarUserManagerHelper.isCurrentProcessUserHasRestriction(DISALLOW_BLUETOOTH)
                ? DISABLED_FOR_USER : AVAILABLE;
    }

    @Override
    protected void updateState(PaneSubPreference preference) {

		// Need to check BT Enable / disable
		if(mBluetoothAdapter == null)
		{
			mBluetoothAdapter = mBluetoothManager.getBluetoothAdapter();
		}
		
		if(mBluetoothManager.getBluetoothAdapter() != null)
		{
			if(mBluetoothManager.getBluetoothAdapter().isEnabled() == false)
			{
				LOG.d("[updateState] BT is disabled");
				preference.setValue(getContext().getString(R.string.humax_bluetooth_off_Status));
				return;
			}
		}
		else
		{
			LOG.e("[updateState] mBluetoothAdapter is Null");
		}
		
        Collection<CachedBluetoothDevice> cachedDevices =
                mBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();

        for (CachedBluetoothDevice cachedDevice : cachedDevices) {
            if (BluetoothDeviceFilter.BONDED_DEVICE_FILTER.matches(cachedDevice.getDevice())) {
				if(cachedDevice.isConnected() == true)
				{
	                preference.setValue(cachedDevice.getName());
					LOG.d("[updateState] BT is connected to" + cachedDevice.getName());
					return; // Bonded device is exist.
				}
				else
				{
					LOG.d("[updateState] not connected device : " + cachedDevice.getName());
				}
            }
        }

		preference.setValue(getContext().getString(R.string.humax_bluetooth_connection_info));
		LOG.d("[updateState] BT is not connected ");
		// set value to "Not connected"
    }
}
