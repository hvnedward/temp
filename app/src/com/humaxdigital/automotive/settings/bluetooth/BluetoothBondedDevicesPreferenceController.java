/*
 * Copyright 2018 The Android Open Source Project
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

import static android.os.UserManager.DISALLOW_CONFIG_BLUETOOTH;

import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;

import com.android.car.settings.R;
import com.android.car.settings.bluetooth.BluetoothDeviceDetailsFragment;
import com.android.car.settings.bluetooth.BluetoothDisconnectConfirmDialogFragment;
import com.android.car.settings.common.FragmentController;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.car.settings.common.Logger;
import android.bluetooth.BluetoothAdapter;
import android.view.Window;

import android.view.WindowManager;


/**
 * Displays a list of bonded (paired) Bluetooth devices. Clicking on a device will attempt a
 * connection with that device. If a device is already connected, a click will prompt the user to
 * disconnect. Devices are shown with an addition affordance which launches the device details page.
 */
public class BluetoothBondedDevicesPreferenceController extends
        BluetoothDevicesGroupPreferenceController {

	private static final Logger LOG = new Logger(BluetoothBondedDevicesPreferenceController.class);
	private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private CachedBluetoothDevice mCachedDevice;
	private final BluetoothConnectionDialog mConDialog = new BluetoothConnectionDialog(getContext(), new BluetoothConnectionListener() {
		@Override
		public void onBtCancelClick() {
			LOG.d("onBtCancel");
		}
	
		@Override
		public void onBtForgetClick() {
			LOG.d("onBtForgetClick");
			mCachedDevice.unpair();
		}
	});


    public BluetoothBondedDevicesPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);

		mConDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected BluetoothDeviceFilter.Filter getDeviceFilter() {
        return BluetoothDeviceFilter.BONDED_DEVICE_FILTER;
    }

    @Override
    protected BluetoothDevicePreference createDevicePreference(CachedBluetoothDevice cachedDevice) {
        BluetoothDevicePreference pref = super.createDevicePreference(cachedDevice);
        if (!getCarUserManagerHelper().isCurrentProcessUserHasRestriction(
                DISALLOW_CONFIG_BLUETOOTH)) {
            pref.setWidgetLayoutResource(R.layout.hmx_bt_device_preference_widget);
/*			
            pref.setOnButtonClickListener(preference -> getFragmentController().launchFragment( BluetoothDeviceDetailsFragment.newInstance(cachedDevice)));
*/

			pref.setOnButtonClickListener(preference -> {
					onConfigIconClicked(cachedDevice);
					});
            pref.showAction(true);
        }
        return pref;
    }

	private void onConfigIconClicked(CachedBluetoothDevice cachedDevice)
	{
		mCachedDevice = cachedDevice;
		mConDialog.setCanceledOnTouchOutside(true);
		mConDialog.setCancelable(true);
		mConDialog.show();
	}

    @Override
    protected void onDeviceClicked(CachedBluetoothDevice cachedDevice) {
	
        if (cachedDevice.isConnected()) {
			
			/*
		            getFragmentController().showDialog(        BluetoothDisconnectConfirmDialogFragment.newInstance(cachedDevice),      null); //  tag 
	                   */
			cachedDevice.disconnect();
        } else {
			LOG.d("[BluetoothBondedDevicesPreferenceController] onDeviceClicked");

			if (mBluetoothAdapter.isDiscovering()) {
					LOG.d("[BluetoothBondedDevicesPreferenceController] mBluetoothAdapter.cancelDiscovery()");
					mBluetoothAdapter.cancelDiscovery();
			}
            cachedDevice.connect(true); // connectAllProfiles= 
        }
    }

    @Override
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedDevice, int bondState) {
        refreshUi();
    }
}
